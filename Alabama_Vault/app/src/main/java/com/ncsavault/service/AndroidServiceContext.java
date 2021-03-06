package com.ncsavault.service;

import android.app.Application;

public class AndroidServiceContext implements ServiceContext {
	private final Application mApp;

	public AndroidServiceContext(Application app) {
		mApp = app;
	}

	@Override
	public Application getApplication() {
		return mApp;
	}
}
