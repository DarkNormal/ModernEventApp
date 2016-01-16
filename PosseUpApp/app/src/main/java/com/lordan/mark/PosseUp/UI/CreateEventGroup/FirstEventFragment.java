package com.lordan.mark.PosseUp.UI.CreateEventGroup;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.lordan.mark.PosseUp.Model.Event;
import com.lordan.mark.PosseUp.R;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Mark on 14/01/2016.
 */
public class FirstEventFragment extends Fragment {
    private Spinner spinner;
    private SwitchCompat mSwitch;
    private View v;
    private Event newEvent = new Event();
    private boolean allDayEvent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        v = inflater.inflate(R.layout.add_event_basic_details, container, false);
        spinner = (Spinner) v.findViewById(R.id.create_event_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.event_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        mSwitch = (SwitchCompat) v.findViewById(R.id.switch_event);
        if(mSwitch != null){
            mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if(isChecked == true){

                    }
                    else{

                    }
                }
            });
        }
        configDateTimeChooser();
        return v;
    }
    public static FirstEventFragment newInstance(String text) {

        FirstEventFragment f = new FirstEventFragment();

        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }

    private void configDateTimeChooser() {
        final EditText startDateInput = (EditText) v.findViewById(R.id.create_event_date);
        final EditText endDateInput = (EditText) v.findViewById(R.id.create_event_date_end);
        configDateChooser(startDateInput, false);
        configDateChooser(endDateInput, true);
        final EditText startTimeInput = (EditText) v.findViewById(R.id.create_event_time);
        final EditText endTimeInput = (EditText) v.findViewById(R.id.create_event_time_end);
        configTimeChooser(startTimeInput, false);
        configTimeChooser(endTimeInput, true);

        final CheckBox allDay = (CheckBox) v.findViewById(R.id.all_day_event);
        allDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    allDayEvent = true;
                    endDateInput.setVisibility(View.INVISIBLE);
                    endTimeInput.setVisibility(View.INVISIBLE);
                } else {
                    endDateInput.setVisibility(View.VISIBLE);
                    endTimeInput.setVisibility(View.VISIBLE);
                }
            }
        });
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
                Calendar mcurrentDate = Calendar.getInstance();
                if (addDay) {
                    mcurrentDate.add(Calendar.DATE, 1);
                }
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedYear, int selectedMonth, int selectedDay) {
                        // TODO Auto-generated method stub
                    /*      Your code   to get date and time    */
                        selectedMonth = selectedMonth + 1;
                        dateTextView.setText("" + selectedDay + "/" + selectedMonth + "/" + selectedYear);
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.show();
            }
        });
    }
    private void configTimeChooser(final EditText timeTextView, final boolean addHour){
        Calendar now = Calendar.getInstance();
        final SimpleDateFormat timeFormat = new SimpleDateFormat("kk:mm");
        timeTextView.setHint(timeFormat.format(now.getTime()));
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
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
                        if (addZeroHour && !addZeroMinute) {
                            timeTextView.setText("0" + selectedHour + ":" + selectedMinute);
                        } else if (!addZeroHour && addZeroMinute) {
                            timeTextView.setText(selectedHour + ":0" + selectedMinute);
                        } else if (addZeroHour && addZeroMinute) {
                            timeTextView.setText("0" + selectedHour + ":0" + selectedMinute);
                        } else {
                            timeTextView.setText(selectedHour + ":" + selectedMinute);
                        }

                    }
                }, hour, minute, true);

                mTimePicker.show();
            }
        });
    }
    public Event getEvent(){
        TextView title =(TextView) v.findViewById(R.id.create_event_title_input);
        TextView startDate =(TextView) v.findViewById(R.id.create_event_date);
        TextView startTime = (TextView) v.findViewById(R.id.create_event_time);
        if(isEmpty(title)){
            title.setError("Title required");
        }
        else if(isEmpty(startDate)){

        }

        startDate = startDate + " " + startTime;
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy kk:mm");
        Date date = null;
        try {
            date = df.parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if(!allDayEvent){
            String endDate =((TextView) v.findViewById(R.id.create_event_date_end)).getText().toString();
            String endTime = ((TextView) v.findViewById(R.id.create_event_time_end)).getText().toString();
            endDate = endDate + " " + endTime;
            Date endingDate = null;
            try {
                endingDate = df.parse(endDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(endingDate);
            newEvent.setEndDateTime(endCal);

        }
        newEvent.setEventName(title);
        newEvent.setStartDateTime(cal);

        return newEvent;
    }
    private boolean isEmpty(TextView v){
        if()
    }
}
