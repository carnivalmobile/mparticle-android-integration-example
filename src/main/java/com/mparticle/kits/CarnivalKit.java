package com.mparticle.kits;

import android.content.Context;
import android.content.Intent;

import com.carnival.sdk.AttributeMap;
import com.carnival.sdk.Carnival;
import com.carnival.sdk.GCMHelper;
import com.google.android.gms.gcm.GcmListenerService;
import com.mparticle.MPEvent;
import com.mparticle.MParticle;
import com.mparticle.internal.MPUtility;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CarnivalKit extends KitIntegration implements KitIntegration.AttributeListener, KitIntegration.EventListener, KitIntegration.PushListener {
    private final String SDK_KEY = "app_key";

    @Override
    protected List<ReportingMessage> onKitCreate(Map<String, String> settings, Context context) {
        Carnival.startEngine(context, getSettings().get(SDK_KEY));
        return null;
    }


    @Override
    public String getName() {
        return "Carnival";
    }



    @Override
    public List<ReportingMessage> setOptOut(boolean optedOut) {
        return null;
    }

    @Override
    public void setUserAttribute(String s, String s1) {
        AttributeMap map = new AttributeMap();
        map.putString(s, s1);
        Carnival.setAttributes(map, null);
    }

    @Override
    public void setUserAttributeList(String s, List<String> list) {
        AttributeMap map = new AttributeMap();
        map.putStringArray(s, new ArrayList<String>(list));
        Carnival.setAttributes(map, null);
    }

    @Override
    public boolean supportsAttributeLists() {
        return true;
    }

    @Override
    public void setAllUserAttributes(Map<String, String> map, Map<String, List<String>> map1) {
        AttributeMap carnivalMap = new AttributeMap();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            carnivalMap.putString(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, List<String>> entry : map1.entrySet()) {
            carnivalMap.putStringArray(entry.getKey(), new ArrayList<String>(entry.getValue()));
        }

        Carnival.setAttributes(carnivalMap, null);
    }

    @Override
    public void removeUserAttribute(String s) {
        Carnival.removeAttribute(s);
    }

    @Override
    public void setUserIdentity(MParticle.IdentityType identityType, String s) {
        if (identityType.equals(MParticle.IdentityType.CustomerId)) {
            Carnival.setUserId(s, null);
        } else if (identityType.equals(MParticle.IdentityType.Email)) {
            Carnival.setUserEmail(s, null);
        } else {
            AttributeMap map = new AttributeMap();
            map.putString(identityType.name(), s);
        }
    }

    @Override
    public void removeUserIdentity(MParticle.IdentityType identityType) {
        if (identityType.equals(MParticle.IdentityType.CustomerId)) {
            Carnival.setUserId(null, null);
        } else if (identityType.equals(MParticle.IdentityType.Email)) {
            Carnival.setUserEmail(null, null);
        } else {
            AttributeMap map = new AttributeMap();
            map.putString(identityType.name(), null);
        }
    }

    @Override
    public List<ReportingMessage> logout() {
        return null;
    }

    @Override
    public List<ReportingMessage> leaveBreadcrumb(String s) {
        return null;
    }

    @Override
    public List<ReportingMessage> logError(String s, Map<String, String> map) {
        return null;
    }

    @Override
    public List<ReportingMessage> logException(Exception e, Map<String, String> map, String s) {
        return null;
    }

    @Override
    public List<ReportingMessage> logEvent(MPEvent mpEvent) {
        Carnival.logEvent(mpEvent.getEventName());
        List<ReportingMessage> messageList = new LinkedList<ReportingMessage>();
        messageList.add(ReportingMessage.fromEvent(this, mpEvent));
        return messageList;
    }

    @Override
    public List<ReportingMessage> logScreen(String s, Map<String, String> map) {
        return null;
    }

    @Override
    public boolean willHandlePushMessage(Intent intent) {
        return intent.getExtras().containsKey("_nid") &&
                MPUtility.isInstanceIdAvailable() &&
                KitUtils.isServiceAvailable(getContext(), GcmListenerService.class);
    }

    @Override
    public void onPushMessageReceived(Context context, Intent intent) {
        Intent service = new Intent(context, com.carnival.sdk.GcmIntentService.class);
        service.setAction("com.google.android.c2dm.intent.RECEIVE");
        service.putExtras(intent);
        context.startService(service);
    }

    @Override
    public boolean onPushRegistration(String token, String senderId) {
        GCMHelper.getInstance().refreshGcmToken();
        return true;
    }
}