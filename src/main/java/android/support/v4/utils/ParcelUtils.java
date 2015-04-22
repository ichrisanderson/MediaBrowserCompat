package android.support.v4.utils;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Util class to provide internal parecelable functions.
 */
public class ParcelUtils {

    private static final String TAG = "ParcelUtils.class";

    // Cache of previously looked up CREATOR.createFromParcel() methods for
    // particular classes.  Keys are the names of the classes, values are
    // Method objects.
    private static final HashMap<ClassLoader,HashMap<String,Parcelable.Creator>>
            mCreators = new HashMap<ClassLoader,HashMap<String,Parcelable.Creator>>();


    /** @hide */
    public static final void writeParcelableCreator(Parcel p, Parcelable pCreator) {
        String name = pCreator.getClass().getName();
        p.writeString(name);
    }

    public static final <T extends Parcelable> T readCreator(Parcel p, Parcelable.Creator<T> creator, ClassLoader loader) {
        if (creator instanceof Parcelable.ClassLoaderCreator<?>) {
            return ((Parcelable.ClassLoaderCreator<T>)creator).createFromParcel(p, loader);
        }
        return creator.createFromParcel(p);
    }

    public static final <T extends Parcelable> Parcelable.Creator<T> readParcelableCreator(
            Parcel p,
            ClassLoader loader) {

        String name = p.readString();
        if (name == null) {
            return null;
        }
        Parcelable.Creator<T> creator;
        synchronized (mCreators) {
            HashMap<String,Parcelable.Creator> map = mCreators.get(loader);
            if (map == null) {
                map = new HashMap<String,Parcelable.Creator>();
                mCreators.put(loader, map);
            }
            creator = map.get(name);
            if (creator == null) {
                try {
                    Class c = loader == null ?
                            Class.forName(name) : Class.forName(name, true, loader);
                    Field f = c.getField("CREATOR");
                    creator = (Parcelable.Creator)f.get(null);
                }
                catch (IllegalAccessException e) {
                    Log.e(TAG, "Illegal access when unmarshalling: "
                            + name, e);
                    throw new BadParcelableException(
                            "IllegalAccessException when unmarshalling: " + name);
                }
                catch (ClassNotFoundException e) {
                    Log.e(TAG, "Class not found when unmarshalling: "
                            + name, e);
                    throw new BadParcelableException(
                            "ClassNotFoundException when unmarshalling: " + name);
                }
                catch (ClassCastException e) {
                    throw new BadParcelableException("Parcelable protocol requires a "
                            + "Parcelable.Creator object called "
                            + " CREATOR on class " + name);
                }
                catch (NoSuchFieldException e) {
                    throw new BadParcelableException("Parcelable protocol requires a "
                            + "Parcelable.Creator object called "
                            + " CREATOR on class " + name);
                }
                catch (NullPointerException e) {
                    throw new BadParcelableException("Parcelable protocol requires "
                            + "the CREATOR object to be static on class " + name);
                }
                if (creator == null) {
                    throw new BadParcelableException("Parcelable protocol requires a "
                            + "Parcelable.Creator object called "
                            + " CREATOR on class " + name);
                }

                map.put(name, creator);
            }
        }

        return creator;
    }

}
