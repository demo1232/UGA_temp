package com.ncsavault.service;


public class VaultService {
	protected ServiceContext context;
	protected ServiceManager serviceManager;

	public VaultService(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
		this.context = serviceManager.getServiceContext();
	}

	@SuppressWarnings("unused")
	protected ServiceContext getContext() {
		return context;
	}

	@SuppressWarnings("unused")
	protected ServiceManager getServiceManager() {
		return serviceManager;
	}

	public void init() {
	}

}
