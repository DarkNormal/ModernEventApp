package com.lordan.mark.PosseUp.UI.CreateEventGroup;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
public class FirstEventFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    @Bind(R.id.all_day_switch) SwitchCompat allDaySwitch;
    @Bind(R.id.online_event_switch) SwitchCompat onlineEventSwitch;
    @Bind(R.id.create_event_title_input) EditText title;
    @Bind(R.id.add_event_desc) EditText description;
    @Bind(R.id.create_event_date) EditText startDateInput;
    @Bind(R.id.create_event_date_end) EditText endDateInput;
    @Bind(R.id.create_event_time) EditText startTimeInput;
    @Bind(R.id.create_event_time_end) EditText endTimeInput;
    @Bind(R.id.create_event_type) AppCompatSpinner visibility;
    //@Bind(R.id.placePickerCard) CardView placeCard;
    @Bind(R.id.add_event_url_layout) TextInputLayout url;
    @Bind(R.id.add_event_location) TextView mAddLocation;

    private View v;
    private Event newEvent;
    private final int PLACE_PICKER_REQUEST = 10;
    private TextView addImage;
    private Place chosenPlace;
    private ImageSelectorDialog dialog;
    private ImageView eventImageView;
    private final String TAG = "FirstEventFragment";
    private Bitmap eventImage;
    private static final int CAMERA_OPTION = 11, GALLERY_OPTION = 12;
    private String[] visibilityTypes;
    private Calendar startingDate = Calendar.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.add_event_basic_details, container, false);
        ButterKnife.bind(this, v);
        addImage = (TextView) v.findViewById(R.id.add_event_image);
        addImage.setOnClickListener(this);
        eventImageView = (ImageView) v.findViewById(R.id.event_image);
        visibilityTypes = getResources().getStringArray(R.array.event_type);
        allDaySwitch.setOnCheckedChangeListener(this);
        onlineEventSwitch.setOnCheckedChangeListener(this);
        mAddLocation.setOnClickListener(this);
        configDateTimeChooser();
        //placeCard.setOnClickListener(this);


        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case 0:
                dialog.dismiss();
                if (resultCode == CAMERA_OPTION) {
                    Log.i(TAG, "got the result from the dialog");

                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 1);//zero can be replaced with any action code
                } else if (resultCode == GALLERY_OPTION) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 2);//one can be replaced with any action code
                }
                break;
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle extras = data.getExtras();
                    eventImage = (Bitmap) extras.get("data");
                    if (eventImageView != null && eventImage != null) {
                        eventImageView.setImageBitmap(eventImage);
                    }
                }
                break;

            case 2:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                        eventImage = createScaledBitmapKeepingAspectRatio(bitmap, 960);
                        bitmap = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (eventImageView != null && eventImage != null) {
                        eventImageView.setImageBitmap(eventImage);

                    }
                }
                break;
            case PLACE_PICKER_REQUEST:
                if (resultCode == -1) {
                    chosenPlace = PlacePicker.getPlace(getContext(), data);
                    String toastMsg = String.format("%s", chosenPlace.getName());
                    mAddLocation.setText(toastMsg);
                    Toast.makeText(getContext(), toastMsg, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private void configDateTimeChooser() {
        //true if a day is to be added
        configDateChooser(startDateInput, false);
        configDateChooser(endDateInput, true);

        configTimeChooser(startTimeInput);
        configTimeChooser(endTimeInput);

    }

    /*
    *
    *
    *
    */
    private void configDateChooser(final EditText dateTextView, final boolean addDay) {
        Calendar today = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("d/M/y");
        if (addDay) {
            today.add(Calendar.DATE, 1);
        }
        dateTextView.setText(format.format(today.getTime()));
        dateTextView.setOnClickListener(this);
    }


    private void configTimeChooser(final EditText timeTextView) {
        Calendar now = Calendar.getInstance();
        final SimpleDateFormat timeFormat = new SimpleDateFormat("kk:mm");
        timeTextView.setText(timeFormat.format(now.getTime()));
        timeTextView.setOnClickListener(this);
    }

    private void addTimeDialog(View view) {
        final EditText timeTextView = (EditText) v.findViewById(view.getId());
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

    private void addDateDialog(View view) {
        final EditText dateTextView = (EditText) v.findViewById(view.getId());
        Calendar mCurrentDate = Calendar.getInstance();
        int mYear = mCurrentDate.get(Calendar.YEAR);
        int mMonth = mCurrentDate.get(Calendar.MONTH);
        int mDay = mCurrentDate.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog mDatePicker;
        mDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedYear, int selectedMonth, int selectedDay) {
                if (dateTextView.getId() == startDateInput.getId()) {
                    Calendar dayAfter = Calendar.getInstance();
                    dayAfter.set(selectedYear, selectedMonth, selectedDay);
                    startingDate.setTime(dayAfter.getTime());
                    dayAfter.add(Calendar.DAY_OF_MONTH, 1);
                    endDateInput.setText(getString(R.string.event_date, dayAfter.get(Calendar.DAY_OF_MONTH), dayAfter.get(Calendar.MONTH) + 1, dayAfter.get(Calendar.YEAR)));
                }
                dateTextView.setText(getString(R.string.event_date, selectedDay, selectedMonth + 1, selectedYear));
            }
        }, mYear, mMonth, mDay);

        if (dateTextView.getId() == endDateInput.getId()) {
            mDatePicker.getDatePicker().setMinDate(startingDate.getTimeInMillis());
        } else {
            mDatePicker.getDatePicker().setMinDate(new Date().getTime());
        }
        mDatePicker.show();
    }


    public Event getEvent() {
        boolean emptyTitle = false;
        boolean emptyDesc = false;

        if (TextUtils.isEmpty(title.getText())) {
            title.setError("Title required");
            emptyTitle = true;
        }
        if (isEmpty(description)) {
            description.setError("Description Required");
            emptyDesc = true;
        }
        if (!emptyTitle && !emptyDesc) {
            String startingDate;
            String endingDate;
            if (allDaySwitch.isChecked()) {
                //all day event, from the beginning of startingDate to the endingDate
                startingDate = startDateInput.getText().toString() + " 00:00";
                endingDate = endDateInput.getText().toString() + " 00:00";
            } else {
                startingDate = startDateInput.getText().toString() + " " + startTimeInput.getText().toString();
                endingDate = endDateInput.getText().toString() + " " + endTimeInput.getText().toString();
            }

            newEvent = new Event(0,
                    title.getText().toString(),
                    description.getText().toString(),
                    visibilityTypes[visibility.getSelectedItemPosition()],
                    allDaySwitch.isChecked(),
                    chosenPlace,
                    onlineEventSwitch.isChecked()
            );
            Date date = parseDate(startingDate);
            Calendar cal = Calendar.getInstance();
            if (date != null) {
                cal.setTime(date);
            }
            newEvent.setStartingCal(cal);
            date = parseDate(endingDate);
            if (date != null) {
                cal.setTime(date);
            }
            newEvent.setEndingCal(cal);
            if (eventImage != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                eventImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                newEvent.setEventImage(encoded);
                byteArray = null;
            }
            return newEvent;
        }
        return null;
    }

    private boolean isEmpty(EditText mEditText) {
        return TextUtils.isEmpty(mEditText.getText());
    }

    @Nullable
    private Date parseDate(String inputDate) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy kk:mm");
        try {
            return df.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bitmap createScaledBitmapKeepingAspectRatio(Bitmap bitmap, int maxSide) {
        Bitmap scaledBitmap;
        final int maxSize = 960;
        int outWidth;
        int outHeight;
        int inWidth = bitmap.getWidth();
        int inHeight = bitmap.getHeight();
        if (inWidth > inHeight) {
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
        switch (v.getId()) {
            case R.id.add_event_image:
                FragmentManager fm = getFragmentManager();
                dialog = new ImageSelectorDialog();
                dialog.setTargetFragment(this, 0);
                dialog.show(fm, "image_selector");
                break;
            case R.id.create_event_date:
            case R.id.create_event_date_end:
                addDateDialog(v);
                break;
            case R.id.create_event_time:
            case R.id.create_event_time_end:
                addTimeDialog(v);
                break;
            case R.id.add_event_location:
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.all_day_switch:
                if (isChecked) {
                    startTimeInput.setVisibility(View.INVISIBLE);
                    endTimeInput.setVisibility(View.INVISIBLE);
                } else {
                    startTimeInput.setVisibility(View.VISIBLE);
                    endTimeInput.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.online_event_switch:
                if (isChecked) {
                    //placeCard.setVisibility(View.INVISIBLE);
                    url.setVisibility(View.VISIBLE);
                } else {
                    //placeCard.setVisibility(View.VISIBLE);
                    url.setVisibility(View.GONE);
                }
                break;

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
