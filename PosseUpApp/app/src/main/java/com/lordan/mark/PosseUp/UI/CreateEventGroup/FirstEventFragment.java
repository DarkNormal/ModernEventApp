package com.lordan.mark.PosseUp.UI.CreateEventGroup;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.lordan.mark.PosseUp.Model.Event;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.UI.ImageSelectorDialog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * Created by Mark on 14/01/2016
 */
public class FirstEventFragment extends Fragment implements View.OnClickListener{
    @Bind(R.id.all_day_switch) SwitchCompat allDaySwitch;
    @Bind(R.id.create_event_date) EditText startDateInput;
    @Bind(R.id.create_event_date_end) EditText endDateInput;
    @Bind(R.id.create_event_time) EditText startTimeInput;
    @Bind(R.id.create_event_time_end) EditText endTimeInput;

    private SwitchCompat mSwitch;
    private View v;
    private final Event newEvent = new Event();
    private boolean allDayEvent;
    private final int PLACE_PICKER_REQUEST = 10;
    private TextView chosenLocation, addImage;
    private Place chosenPlace;
    private ImageSelectorDialog dialog;
    private ImageView eventImageView;
    private final String TAG = "FirstEventFragment";
    private Bitmap eventImage;
    private static final int CAMERA_OPTION = 11, GALLERY_OPTION = 12;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        v = inflater.inflate(R.layout.add_event_basic_details, container, false);
        ButterKnife.bind(this, v);
        chosenLocation =(TextView) v.findViewById(R.id.event_current_location);
        addImage =(TextView) v.findViewById(R.id.add_event_image);
        addImage.setOnClickListener(this);
        eventImageView = (ImageView) v.findViewById(R.id.event_image);
        MaterialSpinner spinner = (MaterialSpinner) v.findViewById(R.id.create_event_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.event_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        allDaySwitch = (SwitchCompat) v.findViewById(R.id.all_day_switch);
        allDaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    startTimeInput.setVisibility(View.INVISIBLE);
                    endTimeInput.setVisibility(View.INVISIBLE);
                }
                else{
                    startTimeInput.setVisibility(View.VISIBLE);
                    endTimeInput.setVisibility(View.VISIBLE);
                }
            }
        });
        spinner.setAdapter(adapter);
        configDateTimeChooser();
        placePickerOnClick();


        return v;
    }
    private void placePickerOnClick(){
        CardView placeCard = (CardView) v.findViewById(R.id.placePickerCard);
        placeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){

            case 0:
                dialog.dismiss();
                if (resultCode == CAMERA_OPTION) {
                    Log.i(TAG, "got the result from the dialog");

                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 1);//zero can be replaced with any action code
                } else if (resultCode == GALLERY_OPTION){
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 2);//one can be replaced with any action code
                }
                break;
            case 1:
                if(resultCode == Activity.RESULT_OK){
                    Bundle extras = data.getExtras();
                    eventImage = (Bitmap) extras.get("data");
                    if(eventImageView != null && eventImage != null) {
                        eventImageView.setImageBitmap(eventImage);
                    }
                }
                break;

            case 2:
                if(resultCode == Activity.RESULT_OK){
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                        eventImage = createScaledBitmapKeepingAspectRatio(bitmap,960);
                        bitmap = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(eventImageView != null && eventImage != null) {
                        eventImageView.setImageBitmap(eventImage);

                    }
                }
                break;
            case PLACE_PICKER_REQUEST:
                if(resultCode == -1){
                    chosenPlace = PlacePicker.getPlace(getContext(), data);
                    String toastMsg  = String.format("%s", chosenPlace.getName());
                    chosenLocation.setText(toastMsg);
                    Toast.makeText(getContext(), toastMsg, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private void configDateTimeChooser() {
        configDateChooser(startDateInput, false);
        configDateChooser(endDateInput, true);
        configTimeChooser(startTimeInput, false);
        configTimeChooser(endTimeInput, true);

    }

    private void configDateChooser(final EditText dateTextView, final boolean addDay){
        Calendar today = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("d/M/y");
        if(addDay){
            today.add(Calendar.DATE, 1);
        }
        dateTextView.setHint(format.format(today.getTime()));
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mCurrentDate = Calendar.getInstance();
                if (addDay) {
                    mCurrentDate.add(Calendar.DATE, 1);
                }
                int mYear = mCurrentDate.get(Calendar.YEAR);
                int mMonth = mCurrentDate.get(Calendar.MONTH);
                int mDay = mCurrentDate.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedYear, int selectedMonth, int selectedDay) {

                        if(dateTextView.getId() == startDateInput.getId()){
                            Calendar dayAfter = Calendar.getInstance();
                            dayAfter.set(selectedYear, selectedMonth, selectedDay);
                            dayAfter.add(Calendar.DAY_OF_MONTH, 1);
                            endDateInput.setText(getString(R.string.event_date, dayAfter.get(Calendar.DAY_OF_MONTH), dayAfter.get(Calendar.MONTH), dayAfter.get(Calendar.YEAR)));
                        }
                        selectedMonth = selectedMonth + 1;
                        dateTextView.setText(getString(R.string.event_date,selectedDay, selectedMonth,selectedYear));
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.show();
            }
        });
    }

    //TODO allow submission of a full day event


    private void configTimeChooser(final EditText timeTextView, final boolean addHour){
        Calendar now = Calendar.getInstance();
        final SimpleDateFormat timeFormat = new SimpleDateFormat("kk:mm");
        timeTextView.setHint(timeFormat.format(now.getTime()));
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mCurrentTime = Calendar.getInstance();
                int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mCurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        boolean addZeroHour = false;
                        boolean addZeroMinute = false;
                        if (selectedHour < 10) {
                            addZeroHour = true;
                        }
                        if (selectedMinute < 10) {
                            addZeroMinute = true;
                        }
                        String timeText;
                        if (addZeroHour && !addZeroMinute) {
                            timeText = "0" + selectedHour + ":" + selectedMinute;

                        } else if (!addZeroHour && addZeroMinute) {
                            timeText = selectedHour + ":0" + selectedMinute;
                        } else if (addZeroHour && addZeroMinute) {
                            timeText = "0" + selectedHour + ":0" + selectedMinute;
                        } else {
                            timeText = selectedHour + ":" + selectedMinute;
                        }
                        timeTextView.setText(timeText);

                    }
                }, hour, minute, true);

                mTimePicker.show();
            }
        });
    }
    public Event getEvent(){
        EditText title =(EditText) v.findViewById(R.id.create_event_title_input);
        EditText startDate =(EditText) v.findViewById(R.id.create_event_date);
        EditText startTime = (EditText) v.findViewById(R.id.create_event_time);
        EditText eventDesc = (EditText) v.findViewById(R.id.add_event_desc);
        boolean emptyTitle = false;
        boolean emptyDate = false;
        boolean emptyDesc = false;
        if(isEmpty(title)){
            title.setError("Title required");
            emptyTitle = true;
        }
        if(isEmpty(startDate) || isEmpty(startTime)){
            startDate.setError("Date & Time Required");
            emptyDate = true;
        }
        if(isEmpty(eventDesc)){
            eventDesc.setError("Description Required");
            emptyDesc = true;
        }
        if(!emptyTitle && !emptyDate && !emptyDesc){
            String startingDate = startDate.getText().toString() + " " + startTime.getText().toString();
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy kk:mm");
            Date date = null;
            try {
                date = df.parse(startingDate);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            newEvent.setStartingCal(cal);
            Calendar endCal = Calendar.getInstance();
            if(!allDayEvent){
                EditText dateEnd = (EditText) v.findViewById(R.id.create_event_date_end);
                EditText timeEnd = (EditText) v.findViewById(R.id.create_event_time_end);
                if(isEmpty(dateEnd) || isEmpty(timeEnd)){
                    dateEnd.setError("Date & Time Required");
                    return null;
                }
                else {

                    String endDate = dateEnd.getText().toString() + " " + timeEnd.getText().toString();
                    Date endingDate = null;
                    try {
                        endingDate = df.parse(endDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    endCal.setTime(endingDate);
                    newEvent.setEndDateTime(endCal);
                }

            }
            else{
               endCal.setTime(cal.getTime());
                endCal.add(Calendar.DATE, 1);
                newEvent.setEndDateTime(endCal);
            }
            newEvent.setEventDesc(eventDesc.getText().toString());
            newEvent.setEventName(title.getText().toString());
            newEvent.setStartDateTime(cal);
            if(chosenPlace != null) {
                newEvent.setPlaceDetails(chosenPlace);
            }
            if(eventImage != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                eventImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                newEvent.setEventImage(encoded);
                byteArray = null;
            }

            return newEvent;
        }
        return null;
    }
    private boolean isEmpty(EditText v){
        return v.getText().toString().equals("");
    }

    private Bitmap createScaledBitmapKeepingAspectRatio(Bitmap bitmap, int maxSide){
        Bitmap scaledBitmap;
        final int maxSize = 960;
        int outWidth;
        int outHeight;
        int inWidth = bitmap.getWidth();
        int inHeight = bitmap.getHeight();
        if(inWidth > inHeight){
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }
        scaledBitmap = Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, false);
        return scaledBitmap;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_event_image:
                FragmentManager fm = getFragmentManager();
                dialog = new ImageSelectorDialog();
                dialog.setTargetFragment(this, 0);
                dialog.show(fm, "image_selector");
                break;
        }
    }
}
