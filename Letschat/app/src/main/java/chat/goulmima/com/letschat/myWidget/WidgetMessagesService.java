package chat.goulmima.com.letschat.myWidget;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

public class WidgetMessagesService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.e("widgetTest","creating widgetMessagesProvider");
        return new WidGetMessagesProvider(getApplicationContext(),intent);
    }
}
