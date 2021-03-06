package org.scm4j.vcs.api.exceptions;

public class EVCSBranchNotFound extends EVCSException {
	
	private static final long serialVersionUID = 1L;

	public EVCSBranchNotFound(String repoUrl, String branchName) {
		super(String.format("branch %s is not found in repo %s", branchName, repoUrl));
	}

}
