package com.diver.diver;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Event {
    private int EventID;
    private Drawable Icon;
    private String Name;
    private String Description;
    private String Music;
    private String DJ;
    private Date Date;
    private String DateText;
    private String Price;
    private Club Club;
    private String ClubName;
    private String ClubDescription;
    private float Lat;
    private float Long;
    private String CityId;
    private String Cachimba;
    private String Address;
    private Integer Hanger;
    private Integer SmokeArea;
    private String Reservado;
    private int MaxPeople;
    private Float BottlePrice=100f, ListasPrice=100f, DoorPrice=100f;
    private String BottleDescription, ListasDescription, DoorDescription;
    private String MaxYears;
    private Integer Promo;

    public int getEventID() {
        return EventID;
    }

    public Event(Context context) {
        EventID = 0;
        Name = "Prueba";
    }

    public String getMaxYears() {
        return MaxYears;
    }

    public void setMaxYears(String maxYears) {
        MaxYears = maxYears;
    }

    public Integer getPromo() {
        return Promo;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public void setPromo(Integer promo) {
        Promo = promo;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setEventID(int eventID) {
        EventID = eventID;
    }

    public Drawable getIcon() {
        return Icon;
    }

    public void setIcon(Drawable icon) {
        Icon = icon;
    }

    public java.util.Date getDate() {
        return Date;
    }

    public void setDate(java.util.Date date) {
        Date = date;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public com.diver.diver.Club getClub() {
        return Club;
    }

    public void setClub(com.diver.diver.Club club) {
        Club = club;
    }

    public String getCachimba() {
        return Cachimba;
    }

    public void setCachimba(String cachimba) {
        Cachimba = cachimba;
    }

    public Integer getHanger() {
        return Hanger;
    }

    public void setHanger(Integer hanger) {
        Hanger = hanger;
    }

    public Integer getSmokeArea() {
        return SmokeArea;
    }

    public void setSmokeArea(Integer smokeArea) {
        SmokeArea = smokeArea;
    }

    public String getReservado() {
        return Reservado;
    }

    public void setReservado(String reservado) {
        Reservado = reservado;
    }

    public String getClubName() {
        return ClubName;
    }

    public void setClubName(String clubName) {
        ClubName = clubName;
    }

    public String getClubDescription() {
        return ClubDescription;
    }

    public void setClubDescription(String clubDescription) {
        ClubDescription = clubDescription;
    }

    public float getLat() {
        return Lat;
    }

    public void setLat(float lat) {
        Lat = lat;
    }

    public float getLong() {
        return Long;
    }

    public void setLong(float aLong) {
        Long = aLong;
    }

    public String getCityId() {
        return CityId;
    }

    public void setCityId(String cityId) {
        CityId = cityId;
    }

    public String getDateText() {
        return DateText;
    }

    public void setDateText(String dateText) {
        DateText = dateText;
    }

    public String getMusic() {
        return Music;
    }

    public void setMusic(String music) {
        Music = music;
    }

    public String getDJ() {
        return DJ;
    }

    public void setDJ(String DJ) {
        this.DJ = DJ;
    }

    public Float getBottlePrice() {
        return BottlePrice;
    }

    public void setBottlePrice(Float bottlePrice) {
        BottlePrice = bottlePrice;
    }

    public Float getListasPrice() {
        return ListasPrice;
    }

    public void setListasPrice(Float listasPrice) {
        ListasPrice = listasPrice;
    }

    public Float getDoorPrice() {
        return DoorPrice;
    }

    public void setDoorPrice(Float doorPrice) {
        DoorPrice = doorPrice;
    }

    public String getBottleDescription() {
        return BottleDescription;
    }

    public void setBottleDescription(String bottleDescription) {
        BottleDescription = bottleDescription;
    }

    public String getListasDescription() {
        return ListasDescription;
    }

    public void setListasDescription(String listasDescription) {
        ListasDescription = listasDescription;
    }

    public String getDoorDescription() {
        return DoorDescription;
    }

    public void setDoorDescription(String doorDescription) {
        DoorDescription = doorDescription;
    }

    public int getMaxPeople() {
        return MaxPeople;
    }

    public void setMaxPeople(int maxPeople) {
        MaxPeople = maxPeople;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        private Event mEvent;
        private TextView tvEventName;
        private TextView tvClubName;
        private TextView tvPrice;
        private TextView tvDate;
        private ImageView ivEvent;
        private View mItemView;
        private Context mContext;
        private CardView cvEvent;

        public EventViewHolder(View itemView, Context context) {
            super(itemView);
            mItemView = itemView;
            mContext = context;
            tvEventName = (TextView) mItemView.findViewById(R.id.tv_event_name);
            tvClubName = (TextView) mItemView.findViewById(R.id.tv_club_name);
            tvPrice = (TextView) mItemView.findViewById(R.id.tv_event_price);
            tvDate = (TextView) mItemView.findViewById(R.id.tv_event_date);
            ivEvent = (ImageView) mItemView.findViewById(R.id.iv_event);
            cvEvent = (CardView) mItemView.findViewById(R.id.cv_event);
        }

        public void bindEvent(final Event event) {
            mEvent = event;
            tvEventName.setText(event.getName());
            Picasso.with(mContext).load("http://diverapp.es/clubs/images/" + event.getEventID() + "event_image.jpg").into(ivEvent);
            tvClubName.setText(event.getClubName());
            if(Integer.valueOf(event.getPrice())<=0){
                tvPrice.setText("gratis");
            }else
            tvPrice.setText(String.valueOf(event.getPrice())+"€");

            SimpleDateFormat dateFormat = new SimpleDateFormat("d   EEE", new Locale("es", "es"));
            String date=(dateFormat.format(event.getDate())).toUpperCase();
            tvDate.setText(date.substring(0,date.length()-1));

            cvEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity)mContext).SharedElementTransition(ivEvent,mContext.getString(R.string.shared_transition_string_iv),event.EventID);
                }
            });

        }
    }


    public static class EventAdapter
            extends RecyclerView.Adapter<EventViewHolder> {

        private List<Event> mEvents;
        private Context mContext;

        public EventAdapter(List<Event> events, Context context) {

            mEvents = events;
            mContext = context;
        }

        @Override
        public EventViewHolder onCreateViewHolder(ViewGroup parent, int pos) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_event, parent, false);

            return new EventViewHolder(view, mContext);
        }

        @Override
        public void onBindViewHolder(Event.EventViewHolder holder, int pos) {

            Event event = mEvents.get(pos);
            holder.bindEvent(event);
        }

        @Override
        public int getItemCount() {
            if (mEvents == null) {
                return 0;
            }
            return mEvents.size();
        }
    }
}
