package virtufridge.virtufridge;

import virtufridge.virtufridge.Remote.IGoogleAPIService;
import virtufridge.virtufridge.Remote.Retrofitclient;

public class Common {

    private static final String GOOGLE_API_URL = "https://maps.googleapis.com";

    public static IGoogleAPIService getGoogleAPIService()
    {
        return Retrofitclient.getClient(GOOGLE_API_URL).create(IGoogleAPIService.class);

    }
}
