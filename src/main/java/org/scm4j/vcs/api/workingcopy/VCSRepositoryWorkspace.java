package org.scm4j.vcs.api.workingcopy;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.FilenameUtils;

public class VCSRepositoryWorkspace implements IVCSRepositoryWorkspace {

	private final IVCSWorkspace workspace;
	private final String repoUrl;
	private File repoFolder;

	protected VCSRepositoryWorkspace(String repoUrl, IVCSWorkspace workspace) {
		this.workspace = workspace;
		this.repoUrl = repoUrl;
		initRepoFolder();
	}

	@Override
	public IVCSWorkspace getWorkspace() {
		return workspace;
	}

	@Override
	public IVCSLockedWorkingCopy getVCSLockedWorkingCopy() {
		return new VCSLockedWorkingCopy(this);
	}

	private String getRepoFolderName() {
		URI uri;
		try {
			uri = new URI(repoUrl.replace("\\", "/"));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		String path = uri.getPath();
		path = path.replaceAll("[^a-zA-Z0-9.-]", "_");
		return FilenameUtils.concat(workspace.getHomeFolder().getPath(), path);
	}

	private void initRepoFolder() {
		String repoFolderName = getRepoFolderName();
		repoFolder = new File(repoFolderName);
		repoFolder.mkdirs();
	}

	@Override
	public File getRepoFolder() {
		return repoFolder;
	}

	@Override
	public String getRepoUrl() {
		return repoUrl;
	}

	@Override
	public String toString() {
		return "VCSRepositoryWorkspace [workspace=" + workspace + ", repoUrl=" + repoUrl + ", repoFolder=" + repoFolder
				+ "]";
	}

}