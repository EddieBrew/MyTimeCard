package com.apps.robertbrewer.stanfordtimecard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.icu.util.Calendar;
import android.os.AsyncTask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.List;
import static android.widget.Toast.*;

public class MainActivity extends AppCompatActivity {
    Calendar date = Calendar.getInstance();//displays the current date when date object appears on screen

    private Button btnDate;
    private Button btnTimeIn;
    private Button btnTimeOut;
    private Button btnSetData;
    private Button btnBDate;
    private Button btnEDate;
    private Button btnGetPayPeriod;

    private EditText txtEventNum;
    private EditText txtEventName;
    private CheckBox checkboxPAID;
    private TextView textViewRegHours;
    private TextView textViewOTHours;
    private TextView textViewYTD;
    private EditText editTextPayrollMessage;

    private int timeSelect; //selects which time to display in Toast message
    private String dailyTimeIn;
    private String dailyTimeOut;
    private HoursDatabase dbInfo;//sqlite database


    private MenuItem item;
    private RelativeLayout activity_main;//Home Layout
    private RelativeLayout query_layout;//Payroll Hours Layout
    private ViewFlipper myViewFlipper;
    private EditText editTextBDate;
    private EditText editTextEDate;
    private int DateSelect;//selects which textView to set the date info
    private ListView payrollList;
    private double cumulativeHours; //holds the total regular and overtime hours value
    HoursArrayAdapter info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        btnDate = (Button) findViewById(R.id.buttonDate);//Home layout: ENTER DATE
        btnTimeIn = (Button) findViewById(R.id.buttonTimeIn);//Home layout: ENTER START TIME
        btnTimeOut = (Button) findViewById(R.id.buttonTimeOut);//Home layout: ENTER END TIME
        btnSetData = (Button) findViewById(R.id.buttonSetData);//Home layout: COMPUTE WORKED HOURS
        txtEventNum = (EditText) findViewById(R.id.editTextEventNum);//Home layout:
        txtEventName = (EditText) findViewById(R.id.editTextEventName);//Home layout
        checkboxPAID = (CheckBox) findViewById(R.id.checkBoxPaid);//Home Layout: Lunch(Check, if Paid)
        textViewRegHours = (TextView) findViewById(R.id.textViewRegHours);//Home Layout
        textViewOTHours = (TextView) findViewById(R.id.textViewOTHours);//Home Layout
        textViewYTD = (TextView) findViewById(R.id.textViewYTD);//Home Layout
        btnBDate = (Button) findViewById(R.id.btnBDate);//Payroll Hours Layout: BEGINNING DATE
        editTextBDate = (EditText) findViewById(R.id.editTextBDate);//Payroll Hours Layout:
        btnEDate = (Button) findViewById(R.id.btnEDate);//Payroll Hours Layout: ENDING DATE
        editTextEDate = (EditText) findViewById(R.id.editTextEDate);//Payroll Hours Layout:
        btnGetPayPeriod = (Button) findViewById(R.id.btnGetPayPeriod);//Payroll Hours Layout: GET PAY HOURS
        editTextPayrollMessage = (EditText) findViewById(R.id.editTextPayrollMessage);//Payroll Hours Layout:


        dbInfo = new HoursDatabase(this);
        getTotalHours();//updates the "Year To Data Hours' value on Gui at startup


////////////////////////////////////////////////////////////////////////////////////////////////////
// main_layout LAYOUT widget
// /////////////////////////////////////////////////////////////////////////////////////////////////
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateSelect = 0;
                setWorkDate();
            }
        });

        btnTimeIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeSelect = 1;
                setTime();
            }
        });

        btnTimeOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeSelect = 2;
                setTime();
            }
        });

        btnSetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processHoursInfo();
                txtEventNum.setText(""); //clears edittext boxes
                txtEventName.setText("");
            }
        });

////////////////////////////////////////////////////////////////////////////////////////////////////
// payroll_query LAYOUT widgets
// /////////////////////////////////////////////////////////////////////////////////////////////////

        btnBDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateSelect = 1;
                setWorkDate();
            }
        });

        btnEDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DateSelect = 2;
                setWorkDate();
            }
        });

        btnGetPayPeriod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payrollList = (ListView) findViewById(R.id.listview_dailyHours);
                   new FetchDatabaseInfo().execute( );
            }
        });
    }

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            date.set(Calendar.YEAR, year);
            date.set(Calendar.MONTH, monthOfYear);
            date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String data = (  (date.get(Calendar.MONTH) + 1) + "/" + date.get(Calendar.DAY_OF_MONTH) + "/"
                    + date.get(Calendar.YEAR));

            switch (DateSelect) {//updates the GUIs date textviews
                case 0:
                    makeText(MainActivity.this, "Your Work Date is " + data, LENGTH_SHORT).show();
                    break;
                case 1:
                    editTextBDate.setText(data);
                    break;
                case 2:
                    editTextEDate.setText(data);
                    break;
                default:
            }
        }
    };


   //////////////////////////////////////////////////////////////////////////////////////////////
   /// /////////////////////////////AsyncTask Method--Background Threading //////////////////////
   //////////////////////////////////////////////////////////////////////////////////////////////
    public class FetchDatabaseInfo extends AsyncTask<Void, Void, List<DailyInfoModel> > {
       public FetchDatabaseInfo() {
           super();
       }

       @Override
       protected List<DailyInfoModel> doInBackground(Void... params) {
           List<DailyInfoModel> dataArray = null;
          dataArray =  dbInfo.getAllInfo(MainActivity.this);
           sortListByDate(dataArray);//reads sqlite database into a List<> and then sort the entries by date

           return dataArray;
       }

       @Override
       protected void onPreExecute() {
           super.onPreExecute();
       }

       @Override
       protected void onPostExecute(List<DailyInfoModel> result) {

           int date1Int = ConvertDateStringToInt(String.valueOf(editTextBDate.getText()));
           int date2Int = ConvertDateStringToInt(String.valueOf(editTextEDate.getText()));
           Integer[] dayImages={
                   R.drawable.sunday,
                   R.drawable.monday,
                   R.drawable.tuesday,
                   R.drawable.wednesday,
                   R.drawable.thursday,
                   R.drawable.friday,
                   R.drawable.saturday,
           };

           List<DailyInfoModel>   sortedList = null;
           sortedList = getPayrollHours(result, date1Int, date2Int);
           if (sortedList.size() > 0) {
               info = new HoursArrayAdapter(MainActivity.this, getPayrollHours(result, date1Int, date2Int), dayImages);
               payrollList = (ListView) findViewById(R.id.listview_dailyHours);
               payrollList.setAdapter(info);

               textViewYTD.setText(String.format("%.2f", cumulativeHours));//main_layout widget
           }
       }

   }///FetchDataInfo class ends
    /////////////////////////////////////////////////////////////////////////////////////////////



    /////////////////////////////////////////////////////////////////////////////////////////////
    /// APP'S MENU BAR METHODS BEGINS
    //////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.menu_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        myViewFlipper = (ViewFlipper) findViewById(R.id.myViewFlipper);

        switch (item.getItemId()) {
            case (R.id.menu_home):// When the Home menu item is clicked, the event below is performed
                myViewFlipper.setDisplayedChild(myViewFlipper.indexOfChild(findViewById(R.id.activity_mainA)));
                return true;
            case (R.id.menu_payrollHours):// When the Payroll Hour menu item is clicked, the event below is performed:
                myViewFlipper.setDisplayedChild(myViewFlipper.indexOfChild(findViewById(R.id.query_layout)));
                return true;
            case (R.id.menu_deleteDatabase)://When the Delete Database menu item is clicked, the event below is performed:
                    deleteAllInfromFromDatabase();
                textViewYTD.setText("0");
                textViewRegHours.setText("0");
                textViewOTHours.setText("0");
                editTextPayrollMessage.setText("");
                info.clear();//clears the listview display

                return true;
            default:
        }//end switch
        return true;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////
    /// APP'S MENU BAR METHODS ENDS
    //////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //APP METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////


    public void setTime() {
        new TimePickerDialog(this, t, date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), true).show();
    }

    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            date.set(Calendar.HOUR_OF_DAY, hourOfDay);
            date.set(Calendar.MINUTE, minute);

            switch (timeSelect) {
                case 1:
                    dailyTimeIn = fillText(date.get(Calendar.HOUR_OF_DAY)) + fillText(date.get(Calendar.MINUTE));
                    Toast.makeText(MainActivity.this, "Your Beginning Time is " + dailyTimeIn, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    dailyTimeOut = fillText(date.get(Calendar.HOUR_OF_DAY)) + fillText(date.get(Calendar.MINUTE));
                    Toast.makeText(MainActivity.this, "Your Ending Time is " + dailyTimeOut, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    /*********************************************************************************
     * String fillText() converts an integer to a string and places a leading zero in
     * the date's month and day, if they consist of a single digit, for proper string
     * representation ( i.e Jan = 1-> 01)
     * @pre none
     * @parameter int
     * @post returns to 2 character String for the month and day
     **********************************************************************************/
    private String fillText(int i) {
        int num = 9;
        return i > num ? i + "" : "0" + i;
    }


    public void setWorkDate() {
        new DatePickerDialog(this, d, date.get(Calendar.YEAR), date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH)).show();
    }


    /*********************************************************************************
     * String processHoursInfo() collects all UI widgets information on the Home layout,
     * process the data  and   place process data into the sqlite database
     * and computes the total working hours stored in the data base
     *
     * @pre none
     * @parameter none
     * @post Updates the Home layouts textViews( Regular Hours,
     *       Over Time Hours and Year To Date Hours)
     **********************************************************************************/
    private void processHoursInfo() {

        double otHours = 0;//overtime hours
        double regHours = 0;//regular hours
        cumulativeHours = Double.parseDouble((String) textViewYTD.getText());

        int timeIN = Integer.parseInt((String) dailyTimeIn);
        int timeOUT = Integer.parseInt((String) dailyTimeOut);
        double totHours = 0;

        if (timeOUT > timeIN) {//hours worked within a 24Hr day
            totHours = computeHoursWorkedA(timeIN, timeOUT);
        } else {
            totHours = computeHoursWorkedB(timeIN, timeOUT);//hours worked within two days
        }

        if (totHours > 8.0) {
            regHours = 8;
            otHours = totHours - 8.;
            otHours = Math.round(otHours * 100) / 100.0d;
        } else {
            regHours = totHours;
            otHours = 0;
        }
        cumulativeHours += totHours;

        String workDay = (date.get(Calendar.MONTH) + 1) + "/" + date.get(Calendar.DAY_OF_MONTH) + "/"
                + date.get(Calendar.YEAR);
        textViewRegHours.setText(String.format("%.2f", regHours));//main_layout widget
        textViewOTHours.setText(String.format("%.2f", otHours));//main_layout widget

        placeInDataBase(workDay, regHours, otHours, getDayOfWeek((date.get(Calendar.DAY_OF_WEEK))) );
        getTotalHours();
    }


    /*********************************************************************************
     * computeHoursWorkedA()computes the daily working hours when the shift is within
     * the same calendar day
     * @pre none
     * @parameter int tin: start time, int tout: end time
     * @post double : total hours worked for the shift
     **********************************************************************************/
    private double computeHoursWorkedA(int tin, int tout) {
        // double hours;
        final double lunchBaseline = 12.0;
        double regHours;
        double partialHour = 0;
        int timeInMins = tin % 100;
        int timeOutMins = tout % 100;

        regHours = (tout - tin) / 100;
        if ((timeInMins < timeOutMins) || (timeInMins == timeOutMins)) { //Timeout mins > timein mins ( timrout = --45, timeIn = --15
            partialHour = ((tout - tin) % 100) / 60.;
        } else {
            partialHour = (60 + timeOutMins - timeInMins) / 60.;
        }

        //main_layout widget
        if ((checkboxPAID.isChecked() == false) && (regHours >= lunchBaseline)) {
            return (regHours + partialHour - 1.0);
        } else if ((checkboxPAID.isChecked() == false) && (regHours < lunchBaseline)) {
            return (regHours + partialHour - 0.5);
        } else {
            return regHours + partialHour;
        }
    } //end method


    /*********************************************************************************
     * computeHoursWorkedB()computes the daily working hours when the shift ends in the
     * next calendar day
     * @pre none
     * @parameter int tin: start time, int tout: end time
     * @post double : total hours worked for the shift
     **********************************************************************************/
    private double computeHoursWorkedB(int tin, int tout) {
        final double lunchBaseline = 12.0;
        int newday = 2360;
        int timeInMins = tin % 100;
        int timeOutMins = tout % 100;
        double partialHour;
        double regHours;

        regHours = (((2400 - tin) + tout) / 100);
        if (timeOutMins < timeInMins) {//
            partialHour = ((newday - tin + tout) % 100) / 60.;//45/60 = .75
            if (partialHour == 0.) {
                regHours++;
            }

        }//end if
        else {
            partialHour = ((timeOutMins - timeInMins) % 100) / 60.;
        }//end else

        //main_layout widget
        if ((checkboxPAID.isChecked() == false) && (regHours >= lunchBaseline)) {
            return (regHours + partialHour - 1.0);
        } else if ((checkboxPAID.isChecked() == false) && (regHours < lunchBaseline)) {
            return (regHours + partialHour - 0.5);
        } else {
            return regHours + partialHour;
        }

    } //end method

    /*********************************************************************************
     * getDayOfWeek() takes the integer represntation of the day,from the Date class
     * and converts it to a String, representing the day of the week
     * @pre none
     * @parameter int intDay: integer representation of the day of the week
     * @post String : string representation of the day of the week
     **********************************************************************************/
    public String getDayOfWeek(int intDay){
        String dayString = "";
        switch(intDay){
            case 1: dayString = "SUN";
                break;
            case 2: dayString = "MON";
                break;
            case 3: dayString = "TUE";
                break;
            case 4: dayString = "WED";
                break;
            case 5: dayString = "THU";
                break;
            case 6: dayString = "FRI";
                break;
            case 7: dayString = "SAT";
                break;
            default:
        }
        return dayString;
    }


    /*********************************************************************************
     * placeInDataBase() populates the DailyInfoMode class variables and inserts
     *  the class into the sqlite database
     * @pre none
     * @parameter String: date, double: regular hours , double: overtime hours
     *            String: day of the week
     * @post none: Adds DailyInfoModel variable to sqlite database
     **********************************************************************************/
    public void placeInDataBase(String workDay, Double regHours, Double otHours, String day) {
        //updates the gui and place the daily entries into the sqlite database
        final int NUM_ENTRIES = 7;
        // String[] databaseEntry;
        double num;

        DailyInfoModel databaseEntry = new DailyInfoModel();
        databaseEntry.setDay(day);
        databaseEntry.setDate(workDay);
        databaseEntry.setEventNumber(String.valueOf(txtEventNum.getText()));
        databaseEntry.setEventName(String.valueOf(txtEventName.getText()));
        databaseEntry.setTime(dailyTimeIn + " - " + dailyTimeOut);
        databaseEntry.setRhours(regHours);
        databaseEntry.setOhours(otHours);

        dbInfo.addNewEntry(MainActivity.this, databaseEntry);
    }

    /*********************************************************************************
     * ConvertDateStringToInt() converts the date string to an integer value
     * @pre none
     * @parameter String: date
     * @post int: converted integer representation of the date
     **********************************************************************************/
    int ConvertDateStringToInt(String date) {

        String delimStr = "/";  //date format is mm/dd/yyyy
        String[] words = date.split(delimStr);

        return ((Integer.parseInt(words[0]) * 1000000) + (Integer.parseInt(words[1]) * 10000) +
                Integer.parseInt(words[2]));

    } //end method


    /*********************************************************************************
     * getPayrollHours() creates a List object containing DailyInfoModel objects
     * ( between user defined dates )and computes the total regular and overtime hours. The List
     * is used to populate a Listview display
     *
     * @pre none
     * @parameter List<>: Sqlite database entries, int: Beginning date parameter ,
     *            int: Endind date parameter
     * @post List<>: List containg DailyInfoModel variables found within defined date parameters
     **********************************************************************************/
    List<DailyInfoModel> getPayrollHours( final List<DailyInfoModel> dataArray, int bDay, int eDay) {
        double rHours = 0;
        double otHours = 0;

        List<DailyInfoModel> timeCardPayPeriod = new ArrayList<>();

            for (DailyInfoModel element : dataArray) {
                if ((ConvertDateStringToInt(element.getDate()) >= bDay) &&
                        (ConvertDateStringToInt(element.getDate()) <= eDay)) {
                    timeCardPayPeriod.add(element);
                    rHours += element.getRhours();
                    otHours += element.getOhours();
                }
        }

        String outMessage = " Payroll from " + editTextBDate.getText() + " to " +editTextEDate.getText() + " are: " + "\n" +
                            " Reg Hours = " + String.format("%.2f", (rHours)) + " and  OT Hours = " +  String.format("%.2f", (otHours));
        editTextPayrollMessage.setText(outMessage);

        return timeCardPayPeriod;
    }

    /***********************************************************************************************
     * getTotalHours() reads a sqlite database file, computes the total worked hours(regular and OT)
     * and updates the Year To Date textview on the Home layout
     * @pre none
     * @parameter none
     * @post none
     * ********************************************************************************************/
    private void getTotalHours() {

        List<DailyInfoModel> timeCard = dbInfo.getAllInfo(MainActivity.this) ;//repopulate with new database

        cumulativeHours = 0;
        double regHours = 0;
        double otHours = 0;

        for (int i = 0; i < timeCard.size(); i++) {

            regHours += timeCard.get(i).getRhours();
            otHours += timeCard.get(i).getOhours();
        }

        cumulativeHours += regHours + otHours;
        textViewYTD.setText(String.format("%.2f", (cumulativeHours)));//main_layout widget

    }//end method

    /***********************************************************************************************
     * sortListByDate() sorts a List, by their dates
     * @pre none
     * @parameter List<> : a list of DailyInfoModel objects
     * @post none
     * ********************************************************************************************/
    private void sortListByDate(List<DailyInfoModel> dataArray) {

        if(dataArray == null){
            return;
        }
        boolean swap = true;
        int j = 0;
        while (swap) {
            swap = false;
            j++;
            for (int i = 0; i < dataArray.size() - j; i++) {
                String item1 = dataArray.get(i).getDate();
                String item2 = dataArray.get(i + 1).getDate();
                int item1Int = ConvertDateStringToInt(item1);
                int item2Int = ConvertDateStringToInt(item2);
                if (item1Int > item2Int) {//swap code
                    DailyInfoModel s = dataArray.get(i);
                    dataArray.set(i, dataArray.get(i + 1));
                    dataArray.set(i + 1, s);
                    swap = true;
                }
            }
        }
    }

    /***********************************************************************************************
     * deleteAllInfromFromDatabase() deletes all entries in the sqlite database
     * @pre none
     * @parameter
     * @post none
     * ********************************************************************************************/
    private void deleteAllInfromFromDatabase(){
        ask(this, "Time Card Daily Info",
                "Do you want to delete?",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { // OK
                        dbInfo.DeleteAll();
                        Toast.makeText(MainActivity.this, "DATA DELETED", Toast.LENGTH_LONG).show();
                        //getTotalHours();

                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { // Cancel
                        Toast.makeText(MainActivity.this, "DELETION CANCELED", Toast.LENGTH_LONG).show();
                    }
                });
    }

    /***********************************************************************************************
     * ask() produces a dialog box when the deleteDatabase menu item is selected
     * @pre none
     * @parameter
     * @post none
     * ********************************************************************************************/
    public static void ask(final Activity activity, String title, String msg,
                           DialogInterface.OnClickListener okListener,
                           DialogInterface.OnClickListener cancelListener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle("DELETE DATABASE");
        alertDialog.setMessage("Do You Really Want To Delete the Entire Database? The Database Can Not be Retrieved after Deletion");
        alertDialog.setPositiveButton("OK", okListener);
        alertDialog.setNegativeButton("CANCEL", cancelListener);
        alertDialog.show();
    }

}//end of class





