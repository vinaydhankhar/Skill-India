package com.example.skillindia;

import android.util.Log;

public class Upload {
    private  String name;
    private  String imageURL;

    public Upload()
    {
     //empty constructor needed
    }
    public Upload(String name, String imageURL)
    {
        if(name.trim().equals(""))
        {
            name="No Name";
        }
        if(imageURL==null)
        {
            imageURL=" ";//some error

        }
        Log.v("Upload",imageURL);
        this.name=name;
        this.imageURL=imageURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getImageURL()
    {
        return  imageURL;
    }
    public void setImageURL(String url)
    {
        imageURL=url;
    }
}
