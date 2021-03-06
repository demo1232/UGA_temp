package com.ncsavault.serviceimpl;


import com.ncsavault.service.ServiceContext;
import com.ncsavault.service.ServiceManager;
import com.ncsavault.service.VaultApiInterface;
import com.ncsavault.service.VaultService;

import java.util.HashMap;

/**
 * Class used for web api call.
 */
public class AbstractServiceManagerImpl implements ServiceManager {
	private final ServiceContext serviceContext;
	private final HashMap<Class<?>, Object> services;

	private static final Class<?>[] DefaultServices = {
			VaultApiCallImpl.class };

	public AbstractServiceManagerImpl(ServiceContext serviceContext)
			throws Exception {
		this(serviceContext, DefaultServices);
	}

	@SuppressWarnings("SameParameterValue")
	private AbstractServiceManagerImpl(ServiceContext serviceContext,
									   Class<?>[] defaultService) throws Exception {
		this.serviceContext = serviceContext;
		services = new HashMap<>();
		for (Class<?> cls : defaultService) {
			VaultService service = (VaultService) cls.getConstructor(
					ServiceManager.class).newInstance(
					AbstractServiceManagerImpl.this);
			service.init();
			services.put(cls, service);
		}
	}

	@Override
	public ServiceContext getServiceContext() {
		return serviceContext;
	}

	@Override
	public VaultApiInterface getVaultService() {
		return (VaultApiInterface) services.get(VaultApiCallImpl.class);
	}

}
