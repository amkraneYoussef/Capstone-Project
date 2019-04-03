package chat.goulmima.com.letschat.POJOS;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class AppUser implements Parcelable {

    public static final String EXTRA_USERNAME = "EXTRA_USERNAME";

    private String AppUserID;
    private String AppUserName;
    private String PhotoUrl;
    private long lastOnline;
    private boolean connected;

    private String FullName;
    private String CurrentCity;

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
        parcel.writeString(FullName);
        parcel.writeString(CurrentCity);
    }

    protected AppUser(Parcel in) {
        AppUserID = in.readString();
        AppUserName = in.readString();
        PhotoUrl = in.readString();
        lastOnline = in.readLong();
        FullName = in.readString();
        CurrentCity = in.readString();
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

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getCurrentCity() {
        return CurrentCity;
    }

    public void setCurrentCity(String currentCity) {
        CurrentCity = currentCity;
    }
}
