package chat.goulmima.com.letschat.myWidget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import chat.goulmima.com.letschat.R;

public class WidgetUpdateService extends IntentService {
    private static final String ACTION_UPDATE_APP_WIDGETS = "ACTION_UPDATE_APP_WIDGETS";


    public WidgetUpdateService(String name) {
        super(name);
    }

    public WidgetUpdateService() {
        super("WidgetUpdateService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent.getAction().equals(ACTION_UPDATE_APP_WIDGETS))
        {
            AppWidgetManager mgr = AppWidgetManager.getInstance(getApplicationContext());
            ComponentName cn = new ComponentName(getApplicationContext(), MessagesListWidget.class);
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn),R.id.gv_lastMessages);

        }
    }

    public static void startActionUpdateAppWidgets(Context context) {
        Intent intent = new Intent(context, WidgetUpdateService.class);
        intent.setAction(ACTION_UPDATE_APP_WIDGETS);
        context.startService(intent);
    }
}
