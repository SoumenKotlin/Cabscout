package com.app.cabscout.controller;

/**
 * Created by rishav on 17/1/17.
 */

public class ModelManager {

    private CabCompaniesManager cabCompaniesManager;
    private RegistrationManager registrationManager;
    private LoginManager loginManager;
    private SearchAddressManager searchAddressManager;
    private LocationDirectionManager locationDirectionManager;
    private static ModelManager modelManager;


    private ModelManager() {
        cabCompaniesManager = new CabCompaniesManager();
        registrationManager = new RegistrationManager();
        searchAddressManager = new SearchAddressManager();
        loginManager = new LoginManager();
        locationDirectionManager = new LocationDirectionManager();
    }

    public static ModelManager getInstance() {
        if (modelManager == null)
            return modelManager = new ModelManager();
        else
            return modelManager;
    }

    public CabCompaniesManager getCabCompaniesManager() {
        return cabCompaniesManager;
    }

    public RegistrationManager getRegistrationManager() {
        return registrationManager;
    }

    public LoginManager getLoginManager() {
        return loginManager;
    }

    public SearchAddressManager getSearchAddressManager() {
        return searchAddressManager;
    }

    public LocationDirectionManager getLocationDirectionManager() {
        return locationDirectionManager;
    }
}
