package com.diver.diver;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import backend.Analytics;
import backend.AskPromoCode;
import backend.SetEventFull;

public class EventDetailFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener, NestedScrollView.OnScrollChangeListener {
    private Event mEvent;
    private float OriginX, OriginY;
    private int mMaxScrollSize;
    private AppBarLayout appBarLayout;
    private View myView;
    private EventDetailFragment mContext = this;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private CoordinatorLayout cl;
    private TextView tvClubName, tvDate, tvDateText, tvMusic, tvDJ, tvCvName, tvDescription;
    private boolean clicked = false;
    private CardView cvName;
    private int maxTopMarginCv = 132;
    private int maxHeightCv = 46;
    private int toolbarHeightCv = 56;
    private int marginRightCv = 14;
    private int marginLeftCv = 14;
    private int marginLeftCvTv;
    private int toolbarMarginLeft = 0;
    private CardView cvData;
    private Toolbar myToolbar;
    private MaterialDialog progressDialog;
    private BottomSheetBehavior mBottomSheetBehaviour;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    private int mCheckedOption;
    private int mPeople = 1, mMaxPeople = 5;
    private float mActualPrice = 0;
    private TextView tvNumber;
    private FloatingActionButton fabLess, fabMore;
    private String mPromoCode;
    private TextView tvTotal;
    private String orderType = "normal";
    private EntryAdapter mAdapter;
    private Bundle mEntry;
    private View vCover;
    private ProgressBar pbEventDetail;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mEvent = EventLab.getEventById(getArguments().getInt("event_id"));
        myView = inflater.inflate(R.layout.fragment_event_detail, container, false);

        Analytics analytics = new Analytics(getActivity());
        analytics.execute("event_seen", "int", String.valueOf(mEvent.getEventID()));

        myToolbar = (Toolbar) myView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        DrawerLayout mDrawerLayout = ((MainActivity) getActivity()).getDrawerLayout();
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("city", mEvent.getCityId());
                ((MainActivity) getActivity()).GoToFragment("home", bundle);
            }
        });


        maxTopMarginCv = StaticUtilities.convertDpToPixel((float) maxTopMarginCv, getActivity());
        maxHeightCv = StaticUtilities.convertDpToPixel((float) maxHeightCv, getActivity());

        marginRightCv = StaticUtilities.convertDpToPixel((float) marginRightCv, getActivity());
        marginLeftCv = StaticUtilities.convertDpToPixel((float) marginLeftCv, getActivity());
        toolbarMarginLeft = StaticUtilities.convertDpToPixel((float) toolbarMarginLeft, getActivity());

        cl = (CoordinatorLayout) myView.findViewById(R.id.cl_event_detail);
        cvData = (CardView) myView.findViewById(R.id.cv_event_data);
        collapsingToolbarLayout = (CollapsingToolbarLayout) myView.findViewById(R.id.ctl_toolbar_layout);
        collapsingToolbarLayout.setTitle("");
        myToolbar.setTitle("");

        //set Views and Layouts
        tvClubName = (TextView) myView.findViewById(R.id.tv_event_detail_club_name);
        tvDate = (TextView) myView.findViewById(R.id.tv_event_detail_date);
        tvDateText = (TextView) myView.findViewById(R.id.tv_event_detail_date_text);
        tvMusic = (TextView) myView.findViewById(R.id.tv_event_detail_music);
        tvDJ = (TextView) myView.findViewById(R.id.tv_event_detail_dj);
        tvCvName = (TextView) myView.findViewById(R.id.tv_name);
        tvDescription = (TextView) myView.findViewById(R.id.tv_event_description);
        cvName = (CardView) myView.findViewById(R.id.cv_event_detail_name);
        pbEventDetail = (ProgressBar) myView.findViewById(R.id.pb_event_detail);

        //*set Views and Layouts

        tvCvName.setText(mEvent.getName());
        tvClubName.setText(mEvent.getClubName());

        mEntry = new Bundle();

        tvTotal = (TextView) myView.findViewById(R.id.tv_total_price);

        //Entries Adapter
        RecyclerView mRecyclerView = (RecyclerView) myView.findViewById(R.id.rv_entries);
        mAdapter = new EntryAdapter(new ArrayList<Bundle>(), getActivity(), this);

        SetEventFull setEntries = new SetEventFull(getActivity(),mEvent, this, mRecyclerView, mAdapter);
        setEntries.execute(mEvent.getEventID());

        return myView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewTreeObserver vto = myView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                CardView.LayoutParams cvParams = (CardView.LayoutParams) tvCvName.getLayoutParams();
                marginLeftCvTv = (cvName.getWidth() - tvCvName.getWidth()) / 2;
                cvParams.leftMargin = marginLeftCvTv;
                tvCvName.setLayoutParams(cvParams);

                collapsingToolbarLayout.setCollapsedTitleTextColor(Color.parseColor("#ffffff"));

                appBarLayout = (AppBarLayout) cl.findViewById(R.id.app_bar);
                NestedScrollView nestedScrollView = (NestedScrollView) cl.findViewById(R.id.nsv_event_detail);
                nestedScrollView.setOnScrollChangeListener(mContext);
                mMaxScrollSize = appBarLayout.getTotalScrollRange();
                appBarLayout.addOnOffsetChangedListener(mContext);
                ViewTreeObserver obs = myView.getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);

                maxHeightCv = cvName.getHeight();
                maxTopMarginCv = appBarLayout.getHeight() - maxHeightCv / 2;
                toolbarHeightCv = myToolbar.getHeight();
            }
        });
    }

    public void LoadData(Event event){

        //Set Views and Layouts
        ImageView ivCollapsing = (ImageView) myView.findViewById(R.id.iv_collapsing_toolbar);
        ImageView ivMap = (ImageView) myView.findViewById(R.id.iv_static_map);

        FrameLayout flCachimba = (FrameLayout) myView.findViewById(R.id.fl_cachimba);
        FrameLayout flReservado = (FrameLayout) myView.findViewById(R.id.fl_reservado);
        FrameLayout flFumar = (FrameLayout) myView.findViewById(R.id.fl_fumar);
        FrameLayout flTerraza = (FrameLayout) myView.findViewById(R.id.fl_terraza);

        ImageView ivTerraza = (ImageView) myView.findViewById(R.id.iv_terraza);
        ImageView ivCachimba = (ImageView) myView.findViewById(R.id.iv_cachimba);
        ImageView ivFumar = (ImageView) myView.findViewById(R.id.iv_fumar);
        ImageView ivReservado = (ImageView) myView.findViewById(R.id.iv_reservado);
        //*Set Views and Layouts

        mSlidingUpPanelLayout = (SlidingUpPanelLayout) myView.findViewById(R.id.spl_drawer);
        myView.findViewById(R.id.ll_bottom_drawer).setVisibility(View.VISIBLE);
        mSlidingUpPanelLayout.setTouchEnabled(false);
        mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        //mSlidingUpPanelLayout.setPanelHeight(mSlidingUpPanelLayout.getHeight());

        vCover = myView.findViewById(R.id.v_cover);
        vCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                vCover.setVisibility(View.GONE);
            }
        });

        mEvent=event;
        pbEventDetail.setVisibility(View.GONE);
        vCover.setVisibility(View.GONE);

        //load data
        myView.findViewById(R.id.ll_event_details).setVisibility(View.VISIBLE);

        tvDescription.setText(mEvent.getDescription());
        tvMusic.setText(mEvent.getMusic());
        tvDJ.setText(mEvent.getDJ());

        if(mEvent.getMusic().equals("-1")) myView.findViewById(R.id.fl_event_detail_music).setVisibility(View.GONE);
        else myView.findViewById(R.id.fl_event_detail_music).setVisibility(View.VISIBLE);

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d 'de' MMMM", new Locale("es", "es"));
        tvDate.setText((dateFormat.format(mEvent.getDate())));
        tvDateText.setText(mEvent.getDateText());

        Picasso.with(getActivity()).load("http://diverapp.es/clubs/images/" + mEvent.getEventID() + "event_image.jpg").into(ivCollapsing);

        String url = "http://maps.googleapis.com/maps/api/staticmap?center="
                + mEvent.getLat() + "," + mEvent.getLong() + "&markers=" + mEvent.getLat() + "," + mEvent.getLong()
                + "&zoom=16&size=1200x300&key=AIzaSyAImYMIPImjNwFDtkFSPxvFQehGnStpb-M";

        Log.v("urlmap", url);
        Picasso.with(getActivity()).load(url).into(ivMap);

        ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics analytics = new Analytics(getActivity());
                analytics.execute("press_event_map", "none");
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + mEvent.getLat() + "," + mEvent.getLong() + "(" + mEvent.getName() + ")");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

        if (mEvent.getCachimba().equals("-1")) flCachimba.setVisibility(View.GONE);
        if (mEvent.getReservado().equals("-1")) flReservado.setVisibility(View.GONE);
        if (mEvent.getTerrace() == -1) flTerraza.setVisibility(View.GONE);
        if (mEvent.getSmokeArea() == -1) flFumar.setVisibility(View.GONE);

        if (mEvent.getSmokeArea() == 0) {
            ivFumar.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_highlight_off_black_24dp));
        } else
            ivFumar.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_done_green_24dp));
        if (mEvent.getTerrace() == 0)
            ivTerraza.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_highlight_off_black_24dp));
        else
            ivTerraza.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_done_green_24dp));
        if (mEvent.getReservado().equals("0"))
            ivReservado.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_highlight_off_black_24dp));
        else
            ivReservado.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_done_green_24dp));
        if (mEvent.getCachimba().equals("0"))
            ivCachimba.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_highlight_off_black_24dp));
        else
            ivCachimba.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_done_green_24dp));

        mMaxPeople = mEvent.getMaxPeople();
        //*load data

        mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        setGetTicketsDialog();
    }


    public static void setMargins(View v, int l, int t, int r, int b, int h, int w) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            p.height = h;
            p.width = w;
            v.requestLayout();
        }
    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        mMaxScrollSize = appBarLayout.getTotalScrollRange();

        float currentScrollPercentage = Math.abs(((verticalOffset * 100f / mMaxScrollSize) / 100f));

        //Title to Toolbar
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) cvName.getLayoutParams();
        params.topMargin = (int) (maxTopMarginCv * (1 - currentScrollPercentage));
        params.height = (int) ((toolbarHeightCv - maxHeightCv) * currentScrollPercentage + maxHeightCv);
        params.leftMargin = (int) (marginLeftCv * (1 - currentScrollPercentage));
        params.rightMargin = (int) (marginRightCv * (1 - currentScrollPercentage));
        cvName.setLayoutParams(params);

        CardView.LayoutParams cvParams = (CardView.LayoutParams) tvCvName.getLayoutParams();
        cvParams.leftMargin = (int) ((toolbarMarginLeft - marginLeftCv) * currentScrollPercentage + marginLeftCvTv);
        tvCvName.setLayoutParams(cvParams);

        LinearLayout.LayoutParams clParams = (LinearLayout.LayoutParams) cvData.getLayoutParams();
        //clParams.topMargin = (int) (StaticUtilities.convertDpToPixel(15, getActivity()) * (1 - currentScrollPercentage) + StaticUtilities.convertDpToPixel(25, getActivity()));
        cvData.setLayoutParams(clParams);

        if (currentScrollPercentage >= 0.97) {
            collapsingToolbarLayout.setContentScrimColor(getActivity().getResources().getColor(R.color.colorPrimary));
            ((TextView) myView.findViewById(R.id.tv_toolbar_title)).setText(mEvent.getName());
            myToolbar.setTitle("");
            cvName.setVisibility(View.GONE);

        } else {
            collapsingToolbarLayout.setContentScrimColor(Color.TRANSPARENT);
            ((TextView) myView.findViewById(R.id.tv_toolbar_title)).setText("");
            cvName.setVisibility(View.VISIBLE);
        }
    }

    public String getCityId() {
        if (mEvent.getCityId() != null) return mEvent.getCityId();
        else return "none";

    }

    private void setGetTicketsDialog() {


        FrameLayout flTicket = (FrameLayout) myView.findViewById(R.id.fl_get_tickets);
        flTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Analytics analytics = new Analytics(getActivity());
                analytics.execute("press_event_detail_get_ticket", "int", String.valueOf(mEvent.getEventID()));
                mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                vCover.setVisibility(View.VISIBLE);
            }
        });

        if(mEvent.getPromo()==0)
            myView.findViewById(R.id.ll_promo).setVisibility(View.GONE);



        mPeople = 1;
        tvNumber = (TextView) myView.findViewById(R.id.tv_number);
        fabLess = (FloatingActionButton) myView.findViewById(R.id.fab_less);
        fabMore = (FloatingActionButton) myView.findViewById(R.id.fab_more);
        //fabLess.setVisibility(View.INVISIBLE);
        fabLess.setEnabled(false);
        fabLess.setVisibility(View.INVISIBLE);
        fabLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPeople <= 1) {
                    mPeople = 1;
                } else {
                    mPeople--;
                }
                if (mPeople <= 1) {
                    fabLess.setEnabled(false);
                    fabLess.setVisibility(View.INVISIBLE);
                } else {
                    fabLess.setEnabled(true);
                    fabLess.setVisibility(View.VISIBLE);
                }
                if (mPeople >= mMaxPeople) {
                    fabMore.setEnabled(false);
                    fabMore.setVisibility(View.INVISIBLE);
                } else {
                    fabMore.setEnabled(true);
                    fabMore.setVisibility(View.VISIBLE);
                }
                tvNumber.setText(String.valueOf(mPeople));
                UpdatePriceBoard();
            }
        });
        fabMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPeople >= mMaxPeople) {
                    mPeople = mMaxPeople;
                } else {
                    mPeople++;
                }
                if (mPeople <= 1) {
                    fabLess.setEnabled(false);
                    fabLess.setVisibility(View.INVISIBLE);
                } else {
                    fabLess.setEnabled(true);
                    fabLess.setVisibility(View.VISIBLE);
                }
                if (mPeople >= mMaxPeople) {
                    fabMore.setEnabled(false);
                    fabMore.setVisibility(View.INVISIBLE);
                } else {
                    fabMore.setEnabled(true);
                    fabMore.setVisibility(View.VISIBLE);
                }
                tvNumber.setText(String.valueOf(mPeople));
                UpdatePriceBoard();
            }
        });

        FrameLayout flPromo = (FrameLayout) myView.findViewById(R.id.fl_use_promo);
        flPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText etPromoCode = (EditText) myView.findViewById(R.id.et_promo_code);
                TextView tvPromoDescription = (TextView) myView.findViewById(R.id.tv_prom_description);
                TextView tvPromoUse = (TextView) myView.findViewById(R.id.tv_promo_use);
                ProgressBar pbPromo = (ProgressBar) myView.findViewById(R.id.pb_promo);
                mPromoCode = etPromoCode.getText().toString();

                mEntry.putString("promo_code",mPromoCode);
                TextInputLayout tilPromo = (TextInputLayout) myView.findViewById(R.id.til_promo_code);

                AskPromoCode askPromoCode = new AskPromoCode(getActivity(), tvPromoDescription, tvTotal, pbPromo, tvPromoUse, mContext, tilPromo);
                askPromoCode.execute(etPromoCode.getText().toString(), mEvent.getEventID());

                //orderType="promotion_dni";
            }
        });

        View flButton = myView.findViewById(R.id.fl_bottom_drawer_buy);
        flButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mEntry.getString("entry_id") != null) {
                    mEntry.putString("people", String.valueOf(mPeople));
                    mEntry.putString("max_years", mEvent.getMaxYears());
                    Bundle bundle = new Bundle();
                    bundle.putString("city", mEvent.getCityId());
                    ((MainActivity) getActivity()).GoToFragment("home", bundle);
                    ((MainActivity) getActivity()).ShowOrderDialog(mEntry, mEvent);
                }
            }
        });
    }

    public void UpdatePriceBoard() {
        TextView tvPriceTicket = (TextView) myView.findViewById(R.id.tv_ticket_price);

        String text;
        Bundle entry = mEntry;

        if (entry.getString("entry_id") != null) {
            if (Float.valueOf(entry.getString("price")) <= 0) text = "gratis";
            else text = entry.getString("price") + "€";
            tvPriceTicket.setText(text);
            if ((Float.valueOf(entry.getString("price")) * mPeople) <= 0) text = "gratis";
            else text = String.valueOf((Float.valueOf(entry.getString("price")) * mPeople)) + "€";
            tvTotal.setText(text);

            TextView tvBuy = (TextView) myView.findViewById(R.id.tv_buy_tickets);

            tvBuy.setText(entry.getString("buy_button_text"));
        }
    }

    public void setOrderType(String type) {
        orderType = type;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    public void setEntry(Bundle entry) {
        mEntry = entry;
    }
}