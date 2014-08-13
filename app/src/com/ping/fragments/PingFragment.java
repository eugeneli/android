package com.ping.fragments;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;
import com.ping.R;
import com.ping.models.Ping;
import com.ping.models.User;
import com.ping.util.FontTools;
import com.ping.util.HtmlBuilder;
import com.ping.util.PingApi;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

public class PingFragment extends Fragment
{	
	public static final String TAG = NewPingFragment.class.getSimpleName();
	
	private PingApi pingApi;
	private Bundle bundle;
	
	private TextView pingTitle;
	private TextView pingAuthor;
	private TextView pingMessage;
	private TextView pingAddress;
	private WebView pingImageWebView;
	
	public static final String PING_DATA = "ping_data";
	
	public static PingFragment newInstance(Ping selectedPing) 
	{
		PingFragment pf = new PingFragment();
		Bundle b = new Bundle();
		b.putParcelable(PingFragment.PING_DATA, selectedPing);
		pf.setArguments(b);
		return pf;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		pingApi = PingApi.getInstance();
		bundle = getArguments();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_ping, container, false);
		FontTools.applyFont(getActivity(), view.findViewById(R.id.root));
		
		pingTitle = (TextView) view.findViewById(R.id.pingTitle);
		pingAuthor = (TextView) view.findViewById(R.id.pingAuthor);
		pingMessage = (TextView) view.findViewById(R.id.pingMessage);
		pingAddress = (TextView) view.findViewById(R.id.pingAddress);
		pingImageWebView = (WebView) view.findViewById(R.id.pingImageWebView);
		
		Ping ping = bundle.getParcelable(PING_DATA);
		
		pingTitle.setText(ping.getTitle());
		pingMessage.setText(ping.getMessage());
		//pingAddress.setText("near " + ping.getAddress());
		
		pingApi.getUserById(ping.getCreatorId(), new FutureCallback<Response<JsonObject>>() {
			@Override
			public void onCompleted(Exception e, Response<JsonObject> response)
			{
				JsonObject jsonResponse = response.getResult().getAsJsonObject(PingApi.RESPONSE);
				String fname = jsonResponse.get(User.FIRST_NAME).getAsString();
				String lname = jsonResponse.get(User.LAST_NAME).getAsString();
				
				pingAuthor.setText(fname + " " + lname);
			}
		});
		
		if(ping.getImageUrlSmall() != null)
			pingImageWebView.loadData(HtmlBuilder.buildImageHtml(ping.getImageUrlSmall()), "text/html; charset=UTF-8", null);
		else
			pingImageWebView.setVisibility(View.GONE);
		
		return view;
	}
}
