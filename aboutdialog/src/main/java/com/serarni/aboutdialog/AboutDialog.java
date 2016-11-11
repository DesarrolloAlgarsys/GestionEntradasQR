package com.serarni.aboutdialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.text.Html;
import android.text.util.Linkify;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AboutDialog extends Dialog
{

	private static Context mContext = null;
	private String mAppName;
    private String mLegalStatement;
    private String mAppVersion;
    private int mResAppIcon;

	@SuppressLint("DefaultLocale")
    public AboutDialog(
            Context context,
            @StringRes int appName,
            String sAppVersion,
            int iAppCode,
            @StringRes int sLegalStatement,
            @DrawableRes int iAppIcon)
	{
		super(context);
		mContext = context;
		//this.setTitle(mAppName);
        mAppName = context.getString(appName);
		if (iAppCode>0){
			mAppVersion = String.format("%s (%d)", sAppVersion, iAppCode);
		}
		else{
			mAppVersion = sAppVersion;
		}
        mLegalStatement = context.getString(sLegalStatement);
        mResAppIcon = iAppIcon;
	}
	
	/*
	* Standard Android on create method that gets called when the activity initialized.
	*/
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.about);

        // app icon
        ImageView imgView = (ImageView) findViewById(R.id.imageView_appIcon);
        imgView.setImageResource(mResAppIcon);

		TextView tv = (TextView)findViewById(R.id.legal_text);
		tv.setText(mLegalStatement==null?readRawTextFile(R.raw.legal):mLegalStatement);
		tv = (TextView)findViewById(R.id.info_text);
        // info text
        String sInfoText = readRawTextFile(R.raw.info);
        if (null!=sInfoText){
            sInfoText = sInfoText
                    .replace("[VERSION]", mAppVersion)
                    .replace("[APP_NAME]", mAppName);
            tv.setText(Html.fromHtml(sInfoText));
        }
		tv.setLinkTextColor(Color.WHITE);
		Linkify.addLinks(tv, Linkify.ALL);
	}
	
	public static String readRawTextFile(int id)
	{
		InputStream inputStream = mContext.getResources().openRawResource(id);
		InputStreamReader in = new InputStreamReader(inputStream);
		BufferedReader buf = new BufferedReader(in);
		String line;
		StringBuilder text = new StringBuilder();
		try 
		{

		while (( line = buf.readLine()) != null) text.append(line);
		} catch (IOException e)
		{
			return null;
		}
		return text.toString();
	}
}

