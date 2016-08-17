package com.projectkaiser.scm.vcs.api.abstracttest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.projectkaiser.scm.vcs.api.IVCS;
import com.projectkaiser.scm.vcs.api.exceptions.EVCSBranchExists;
import com.projectkaiser.scm.vcs.api.workingcopy.IVCSLockedWorkingCopy;
import com.projectkaiser.scm.vcs.api.workingcopy.IVCSRepositoryWorkspace;
import com.projectkaiser.scm.vcs.api.workingcopy.IVCSWorkspace;
import com.projectkaiser.scm.vcs.api.workingcopy.VCSWorkspace;

/**
 * The Class VCSAbstractTest.
 */
public abstract class VCSAbstractTest {
	
	/** The Constant WORKSPACE_DIR. */
	public static final String WORKSPACE_DIR = System.getProperty("java.io.tmpdir") + "pk-vcs-workspaces";
	
	/** The Constant TEST_REPO_URL. */
	public static final String TEST_REPO_URL = "c:/test/utils/";
	
	/** The repo name. */
	public static String REPO_NAME;
	
	/** The Constant NEW_BRANCH. */
	public static final String NEW_BRANCH = "new-branch";
	
	/** The Constant SRC_BRANCH. */
	public static final String SRC_BRANCH = "master";
	
	/** The Constant CREATED_DST_BRANCH_COMMIT_MESSAGE. */
	public static final String CREATED_DST_BRANCH_COMMIT_MESSAGE = "created dst branch";
	
	/** The Constant DELETE_BRANCH_COMMIT_MESSAGE. */
	public static final String DELETE_BRANCH_COMMIT_MESSAGE = "deleted";
	
	/** The repo name. */
	protected String repoName;
	
	/** The repo url. */
	protected String repoUrl;
	
	/** The local VCS repo. */
	protected IVCSRepositoryWorkspace localVCSRepo;
	
	/** The local VCS workspace. */
	protected IVCSWorkspace localVCSWorkspace;
	
	/** The mocked VCS repo. */
	protected IVCSRepositoryWorkspace mockedVCSRepo;
	
	/** The mocked LWC. */
	protected IVCSLockedWorkingCopy mockedLWC;
	
	/** The vcs. */
	protected IVCS vcs;

	/**
	 * Gets the vcs.
	 *
	 * @return the vcs
	 */
	public IVCS getVcs() {
		return vcs;
	}

	/**
	 * Sets the vcs.
	 *
	 * @param vcs the new vcs
	 */
	public void setVcs(IVCS vcs) {
		this.vcs = vcs;
	}

	/**
	 * Sets the up and tear down.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Before
	@After
	public void setUpAndTearDown() throws IOException {
		FileUtils.deleteDirectory(new File(WORKSPACE_DIR));
	}
	
	/**
	 * Sets the up.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Before
	public void setUp() throws IOException {
		REPO_NAME = "pk-vcs-" + getVCSTypeString() + "-testrepo";
		
		String uuid = UUID.randomUUID().toString();
		repoName = (REPO_NAME + "_" + uuid);
		
		repoUrl = getVCSTypeString() + "." + getVCSRepoUrl() + repoName;
		
		localVCSWorkspace = new VCSWorkspace(WORKSPACE_DIR);
		localVCSRepo = localVCSWorkspace.getVCSRepositoryWorkspace(repoUrl);
		
		mockedLWC = Mockito.spy(localVCSRepo.getVCSLockedWorkingCopy());
		Mockito.doReturn(mockedLWC).when(mockedVCSRepo).getVCSLockedWorkingCopy();
		
		createVCS(mockedVCSRepo);
	}
	
	/**
	 * Reset mocks.
	 */
	protected void resetMocks() {
		Mockito.reset(mockedVCSRepo);
		mockedLWC = Mockito.spy(localVCSRepo.getVCSLockedWorkingCopy());
		Mockito.doReturn(mockedLWC).when(mockedVCSRepo).getVCSLockedWorkingCopy();
	}
	
	/**
	 * Test create branch.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testCreateBranch() throws Exception {
		vcs.createBranch(SRC_BRANCH, NEW_BRANCH, CREATED_DST_BRANCH_COMMIT_MESSAGE);
		Mockito.verify(mockedVCSRepo).getVCSLockedWorkingCopy();
		Mockito.verify(mockedLWC).close();
		Thread.sleep(2000); // next operation fails time to time. Looks like github has some latency on branch operations
		
		assertTrue(getBranches().contains(NEW_BRANCH));
		assertTrue(getBranches().size() == 2); // master & NEW_BRANCH
		
		try {
			vcs.createBranch(SRC_BRANCH, NEW_BRANCH, CREATED_DST_BRANCH_COMMIT_MESSAGE);
			fail("\"Branch exists\" situation not detected");
		} catch (EVCSBranchExists e) {
		}
		resetMocks();
		vcs.deleteBranch(NEW_BRANCH, DELETE_BRANCH_COMMIT_MESSAGE);
		Mockito.verify(mockedVCSRepo).getVCSLockedWorkingCopy();
		Mockito.verify(mockedLWC).close();
		Thread.sleep(2000); // next operation fails from time to time. Looks like github has some latency on branch operations
		assertTrue (getBranches().size() == 1);
	}
	
	/**
	 * Gets the VCS type string.
	 *
	 * @return the VCS type string
	 */
	public abstract String getVCSTypeString();
	
	/**
	 * Gets the VCS repo url.
	 *
	 * @return the VCS repo url
	 */
	protected abstract String getVCSRepoUrl();
	
	/**
	 * Creates the VCS.
	 *
	 * @param mockedVCSRepo the mocked VCS repo
	 */
	protected abstract void createVCS(IVCSRepositoryWorkspace mockedVCSRepo);
	
	/**
	 * Gets the branches.
	 *
	 * @return the branches
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected abstract Set<String> getBranches() throws IOException;
}

