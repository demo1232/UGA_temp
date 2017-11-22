package com.ncsavault.service;

public interface ServiceManager {
	ServiceContext getServiceContext();

	VaultApiInterface getVaultService();

}
