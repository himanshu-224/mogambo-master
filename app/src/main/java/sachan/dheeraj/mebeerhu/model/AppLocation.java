package sachan.dheeraj.mebeerhu.model;

/**
 * Created by agarwalh on 9/12/2015.
 */
public class AppLocation {
    private String locationName;
    private String locDescription;

    public AppLocation(String name)
    {
        locationName = name;
        locDescription = "";
    }
    public void createSetDescription(String addrDetails, String locality, String city, String state, String country, String pincode)
    {
        locDescription = locationName + ", " + addrDetails + ", " + locality + ", " + city + ", " + state + ", " + country + " - " + pincode;
    }
    public String getLocationName()
    {
        return locationName;
    }
    public void setLocationName(String name)
    {
        locationName = name;
    }
    public String getLocDescription()
    {
        return locDescription;
    }
    public void setLocDescription(String description)
    {
        locDescription = description;
    }
}
