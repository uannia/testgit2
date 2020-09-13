

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;


public class AdHolderOnline extends DialogUntil {






    public void setListAd(HashMap<String, Stack<AdsChild>> mapAd) {
        Constants.map = mapAd;
    }

    private Activity activity;

    public interface AdHolderCallback {
        void onAdShow(@AdDef.NETWORK String network, @AdDef.AD_TYPE String adtype);

        void onAdClose(@AdDef.NETWORK String adType);

        void onAdFailToLoad(String messageError);

        void onAdOff();
    }

    public AdHolderOnline(Activity activity) {
        this.activity = activity;
    }

    public boolean isDebugMode(){
        return Constants.isDebugMode;
    }

    public void showAds(String spaceName, LinearLayout layoutContainer, String textLoading, AdHolderCallback callback) {
        if (!textLoading.equals("")) {
            showDialog(activity, textLoading);
        }

        new LoadData().getAdChild(activity, spaceName, new LoadData.ServerCallback() {
            @Override
            public void onLoadDone(Stack<AdsChild> list) {
                holderAds(list, layoutContainer, textLoading, callback);
            }

            @Override
            public void onLoadFail(String message) {
                if (!textLoading.equals("")) {
                    hideDialog();
                }
                if (message == null) message = "";
                if (Constants.isDebugMode) {
                    Toast.makeText(activity, "ADSERVER_ERROR: " + message, Toast.LENGTH_SHORT).show();
                }
                callback.onAdFailToLoad(message);

            }
        });
    }

    public void showAdsTotalOffline(HashMap<String, Stack<AdsChild>> map, String spaceName, LinearLayout layoutContainer, String textLoading, AdHolderCallback callback) {
        if (!textLoading.equals("")) {
            showDialog(activity, textLoading);
        }

        new CheckInternetConnection().runToCheckInternet(activity, new CheckInternetConnection.InternetCallback() {
            @Override
            public void onInernetAvailable() {
                Stack<AdsChild> list = map.get(spaceName);
                if (list != null) {
                    Stack<AdsChild> finalList = new Stack<>();
                    if (AdConstant.testAd == TestAd.GOOGLE) {
                        for (AdsChild adsChild : list) {
                            if (adsChild.getNetwork().equals(AdDef.NETWORK.GOOGLE)) {
                                finalList.add(adsChild);
                            }
                        }
                    } else if (AdConstant.testAd == TestAd.FACEBOOK) {
                        for (AdsChild adsChild : list) {
                            if (adsChild.getNetwork().equals(AdDef.NETWORK.FACEBOOK)) {
                                finalList.add(adsChild);
                            }
                        }
                    } else {
                        finalList.addAll(list);
//                        finalList = list;
                    }
                    holderAds(finalList, layoutContainer, textLoading, callback);
                }
            }

            @Override
            public void onInternetDisconnected(String messageError) {
                if (!textLoading.equals("")) {
                    hideDialog();
                }
                if (callback != null) callback.onAdFailToLoad(AdConstant.textNoInternet);
            }
        });




 


}
