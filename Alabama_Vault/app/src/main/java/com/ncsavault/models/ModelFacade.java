package com.ncsavault.models;

/**
 *
 * ModelFacade class which holds the reference of RemoteModel and LocalModel
 * RemoteModel is a class which communicate with server
 * LocalModel is a class which communicate with data
 * This class is very help ful for writing an application which needs to work offline and online
 * both mode
 */

public class ModelFacade implements IModel {


    private final LocalModel localModel;

    private final RemoteModel remoteModel;


    public ModelFacade() {
        localModel = new LocalModel();
        remoteModel = new RemoteModel();
    }

    public LocalModel getLocalModel() {
        return localModel;
    }

    public RemoteModel getRemoteModel() {
        return remoteModel;
    }

    @Override
    public void initialize() {

        localModel.initialize();
        remoteModel.initialize();
    }

    @Override
    public void destroy() {

        localModel.destroy();
        remoteModel.destroy();
    }
}
