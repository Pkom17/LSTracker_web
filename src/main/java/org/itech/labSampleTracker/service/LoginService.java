package org.itech.labSampleTracker.service;

public interface LoginService {
	boolean isAuthenticated();

	void login(String username, String password);
}
