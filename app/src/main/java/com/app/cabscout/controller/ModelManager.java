package com.app.cabscout.controller;

/**
 * Created by rishav
 * on 17/1/17.
 */

public class ModelManager {

    private CabCompaniesManager cabCompaniesManager;
    private RegistrationManager registrationManager;
    private LoginManager loginManager;
    private FacebookLoginManager facebookLoginManager;
    private SearchAddressManager searchAddressManager;
    private LocationDirectionManager locationDirectionManager;
    private LocationService locationService;
    private DateTimeManager dateTimeManager;
    private RequestRideManager requestManager;
    private ScheduleHistoryManager scheduleHistoryManager;
    private TripsHistoryManager tripsHistoryManager;
    private AddHomeManager addHomeManager;
    private AddWorkManager addWorkManager;
    private ImageUploadManager imageUploadManager;
    private AllowDriversManager allowDriversManager;
    private NearbyDriversManager nearbyDriversManager;
    private ChangePasswordManager changePasswordManager;
    private CancelRequestManager cancelRequestManager;

    private static ModelManager modelManager;


    private ModelManager() {
        cabCompaniesManager = new CabCompaniesManager();
        registrationManager = new RegistrationManager();
        searchAddressManager = new SearchAddressManager();
        loginManager = new LoginManager();
        facebookLoginManager = new FacebookLoginManager();
        locationDirectionManager = new LocationDirectionManager();
        locationService = new LocationService();
        dateTimeManager = new DateTimeManager();
        requestManager = new RequestRideManager();
        scheduleHistoryManager = new ScheduleHistoryManager();
        tripsHistoryManager = new TripsHistoryManager();
        addHomeManager = new AddHomeManager();
        addWorkManager = new AddWorkManager();
        imageUploadManager = new ImageUploadManager();
        allowDriversManager = new AllowDriversManager();
        nearbyDriversManager = new NearbyDriversManager();
        changePasswordManager = new ChangePasswordManager();
        cancelRequestManager = new CancelRequestManager();
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

    public FacebookLoginManager getFacebookLoginManager() {
        return facebookLoginManager;
    }

    public SearchAddressManager getSearchAddressManager() {
        return searchAddressManager;
    }

    public LocationDirectionManager getLocationDirectionManager() {
        return locationDirectionManager;
    }

    public LocationService getLocationService() {
        return locationService;
    }

    public DateTimeManager getDateTimeManager() {
        return dateTimeManager;
    }

    public RequestRideManager getRequestManager() {
        return requestManager;
    }

    public ScheduleHistoryManager getScheduleHistoryManager() {
        return scheduleHistoryManager;
    }

    public TripsHistoryManager getTripsHistoryManager() {
        return tripsHistoryManager;
    }

    public AddHomeManager getAddHomeManager() {
        return addHomeManager;
    }

    public AddWorkManager getAddWorkManager() {
        return addWorkManager;
    }

    public ImageUploadManager getImageUploadManager() {
        return imageUploadManager;
    }

    public AllowDriversManager getAllowDriversManager() {
        return allowDriversManager;
    }

    public NearbyDriversManager getNearbyDriversManager() {
        return nearbyDriversManager;
    }

    public ChangePasswordManager getChangePasswordManager() {
        return changePasswordManager;
    }

    public CancelRequestManager getCancelRequestManager() {
        return cancelRequestManager;
    }
}
