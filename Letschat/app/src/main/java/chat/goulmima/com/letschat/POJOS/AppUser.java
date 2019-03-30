package chat.goulmima.com.letschat.POJOS;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class AppUser implements Parcelable {

    private String AppUserID;
    private String AppUserName;
    private String PhotoUrl;
    private long lastOnline;
    private boolean connected;

    public AppUser() {
    }

    public String getAppUserName() {
        return AppUserName;
    }

    public void setAppUserName(String appUserName) {
        AppUserName = appUserName;
    }

    public String getPhotoUrl() {
        return PhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        PhotoUrl = photoUrl;
    }

    public long getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(long lastOnline) {
        this.lastOnline = lastOnline;
    }

    public boolean getConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public String getAppUserID() {
        return AppUserID;
    }

    public void setAppUserID(String appUserID) {
        AppUserID = appUserID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(AppUserID);
        parcel.writeString(AppUserName);
        parcel.writeString(PhotoUrl);
        parcel.writeLong(lastOnline);
    }

    protected AppUser(Parcel in) {
        AppUserID = in.readString();
        AppUserName = in.readString();
        PhotoUrl = in.readString();
        lastOnline = in.readLong();
    }

    public static final Creator<AppUser> CREATOR = new Creator<AppUser>() {
        @Override
        public AppUser createFromParcel(Parcel in) {
            return new AppUser(in);
        }

        @Override
        public AppUser[] newArray(int size) {
            return new AppUser[size];
        }
    };
}
