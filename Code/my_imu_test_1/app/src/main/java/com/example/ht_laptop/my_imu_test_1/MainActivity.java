package com.example.ht_laptop.my_imu_test_1;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.FloatProperty;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private MySensorEventListener mySensorEventListener;
    private SensorManager mSensorManager;
    private Sensor mAcc;
    private Sensor mGyro;
    private Sensor mComp;

    private Float[] offset_acc;
    private Float[] value_acc;
    private Float[] old_value_acc;
    private Float[] max_value_calibration_acc;
    private Float[] min_value_calibration_acc;
    private boolean flag_busy_calibration_acc;
    private int limit_sample_calibration_acc;
    private int number_sample_calibration_acc;
    private float time_calibration_acc;

    private MyCompass myCompass;
    private Float[] value_calibration_compass_x;
    private Float[] value_calibration_compass_y;
    private Float[] value_calibration_compass_z;
    private int limit_sample_calibration_compass;
    private int number_sample_calibration_compass;
    private boolean flag_busy_calibration_compass;
    private boolean flag_enable_run;
    private float value_init_compass_x;
    private float value_init_compass_y;
    private float value_init_compass_z;
    private FIRFilter[] firFilterCompass;
    private FIRFilter[] firFilterAcc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myCompass = new MyCompass();
        flag_busy_calibration_compass = false;
        flag_enable_run = false;
        value_calibration_compass_x = null;
        value_calibration_compass_y = null;
        value_calibration_compass_z = null;
        firFilterCompass = new FIRFilter[3];
        firFilterAcc = new FIRFilter[3];

        Float[] parameter_fir_filter = {0.000138935361290912f,0.000130266471559578f,
                0.00018664506118285f,0.000254664651198495f,0.000334344078318736f,
                0.000424938129827918f,0.000524999352212975f,0.000632296630256393f,
                0.00074375969912471f,0.000855044923044966f,0.000961408230436814f,
                0.00105671280511205f,0.00113433387460142f,0.00118676760559184f,
                0.001206019140551f,0.00118387388117777f,0.0011120390722072f,
                0.000982541154626056f,0.000788076861582903f,0.000522280117463299f,
                0.000180282243162568f,-0.000241042965962667f,-0.000742622039603985f,
                -0.00132283513334626f,-0.00197729161555272f,-0.00269847309672479f,
                -0.00347565109410741f,-0.00429473683754866f,-0.005138306321372f,
                -0.00598574306610401f,-0.00681346026965773f,-0.00759524867538627f,
                -0.00830277817677093f,-0.00890609121448887f,-0.00937436083530168f,
                -0.00967656313174888f,-0.00978237319789904f,-0.0096629992204582f,
                -0.00929209960895449f,-0.0086466529235423f,-0.00770783912828102f,
                -0.00646179725704285f,-0.00490040071448609f,-0.00302179510559932f,
                -0.00083093151651953f,0.00166018306053752f,0.00443231599496835f,
                0.00745909552753467f,0.0107072388552826f,0.014137018183269f,
                0.0177028619954403f,0.0213542019766168f,0.0250364022126926f,
                0.028691911275547f,0.0322614485654506f,0.0356853351533003f,
                0.0389048237598247f,0.0418634709526064f,0.0445084545492563f,
                0.0467918452670318f,0.0486717494077353f,0.0501133428395909f,
                0.051089712757056f,0.0515825377149908f,0.0515825377149908f,
                0.051089712757056f,0.0501133428395909f,0.0486717494077353f,
                0.0467918452670318f,0.0445084545492563f,0.0418634709526064f,
                0.0389048237598247f,0.0356853351533003f,0.0322614485654506f,
                0.028691911275547f,0.0250364022126926f,0.0213542019766168f,
                0.0177028619954403f,0.014137018183269f,0.0107072388552826f,
                0.00745909552753467f,0.00443231599496835f,0.00166018306053752f,
                -0.00083093151651953f,-0.00302179510559932f,-0.00490040071448609f,
                -0.00646179725704285f,-0.00770783912828102f,-0.0086466529235423f,
                -0.00929209960895449f,-0.0096629992204582f,-0.00978237319789904f,
                -0.00967656313174888f,-0.00937436083530168f,-0.00890609121448887f,
                -0.00830277817677093f,-0.00759524867538627f,-0.00681346026965773f,
                -0.00598574306610401f,-0.005138306321372f,-0.00429473683754866f,
                -0.00347565109410741f,-0.00269847309672479f,-0.00197729161555272f,
                -0.00132283513334626f,-0.000742622039603985f,-0.000241042965962667f,
                0.000180282243162568f,0.000522280117463299f,0.000788076861582903f,
                0.000982541154626056f,0.0011120390722072f,0.00118387388117777f,
                0.001206019140551f,0.00118676760559184f,0.00113433387460142f,
                0.00105671280511205f,0.000961408230436814f,0.000855044923044966f,
                0.00074375969912471f,0.000632296630256393f,0.000524999352212975f,
                0.000424938129827918f,0.000334344078318736f,0.000254664651198495f,
                0.00018664506118285f,0.000130266471559578f,0.000138935361290912f};
        /*
        Float[] parameter_fir_filter = {-9.5752605466603e-06f,1.14053084753113e-06f,
                1.21185270930909e-06f,1.36612292422125e-06f,1.60141328600742e-06f,
                1.91395978260434e-06f,2.30553271878356e-06f,2.77554917169069e-06f,
                3.32837274322472e-06f,3.9654510777885e-06f,4.69288416706982e-06f,
                5.51383082956111e-06f,6.43585100707017e-06f,7.46302035608228e-06f,
                8.60429217413407e-06f,9.86490402815922e-06f,1.12544303321693e-05f,
                1.27793254538403e-05f,1.44499784426458e-05f,1.62735666868895e-05f,
                1.82615229911579e-05f,2.04216473604631e-05f,2.27663570560532e-05f,
                2.53041217751702e-05f,2.80482069458485e-05f,3.10078416300241e-05f,
                3.41970127627159e-05f,3.76257103637768e-05f,4.13087235793786e-05f,
                4.52564995216614e-05f,4.94850355640381e-05f,5.40049942485039e-05f,
                5.88334659753868e-05f,6.39817111679586e-05f,6.94677392105823e-05f,
                7.53030022037011e-05f,8.15069953090302e-05f,8.80908920993511e-05f,
                9.50761189756892e-05f,0.000102472352648184f,0.000110305105582749f,
                0.00011858488271745f,0.000127318515832955f,0.000136557693632332f,
                0.000146286563264041f,0.000156532956006339f,0.000167313729781744f,
                0.000178651614255598f,0.000190563098352905f,0.000203068219724462f,
                0.000216184332963057f,0.000229930935970451f,0.000244326216071779f,
                0.000259389796947092f,0.000275140684189231f,0.000291597869938214f,
                0.000308780932649348f,0.000326708879290118f,0.000345400869926808f,
                0.000364875996846684f,0.000385153271881389f,0.000406251324734714f,
                0.000428189160082477f,0.000450984809951676f,0.000474657120901507f,
                0.000499223470787463f,0.000524702286686557f,0.000551110329713203f,
                0.00057846533074219f,0.000606783353701641f,0.000636081420346253f,
                0.000666374509056456f,0.000697679145994846f,0.000730008986456393f,
                0.000763379735305123f,0.000797803936392077f,0.000833296223627447f,
                0.000869867550163783f,0.000907531736133415f,0.000946297770988519f,
                0.000986178938186383f,0.00102718116267983f,0.00106931822540122f,
                0.00111259467879384f,0.00115701579035543f,0.00120259568667747f,
                0.00124933294849455f,0.00129723349634724f,0.00134630073961758f,
                0.00139653874997478f,0.00144794847549755f,0.00150053049787485f,
                0.00155428377809904f,0.0016092064604517f,0.00166529545905812f,
                0.00172254686802054f,0.00178095593211534f,0.00184051553627144f,
                0.00190121885598122f,0.0019630565946842f,0.00202601882095278f,
                0.00209009409920761f,0.0021552698957957f,0.00222153210180776f,
                0.00228886578435181f,0.00235725407108704f,0.00242667960338182f,
                0.00249712273008277f,0.00256856350415117f,0.00264097966862761f,
                0.00271434853135328f,0.00278864525421695f,0.00286384456068003f,
                0.00293991879427246f,0.00301684037202565f,0.00309457881627437f,
                0.00317310402903081f,0.00325238301385568f,0.00333238315202735f,
                0.00341306869342068f,0.00349440485207333f,0.00357635304387541f,
                0.00365887687000188f,0.0037419341099305f,0.00382548754929838f,
                0.00390949298701744f,0.00399390727716584f,0.00407868915843243f,
                0.00416379190638012f,0.00424916957015157f,0.0043347756819607f,
                0.00442056298988981f,0.00450648293862937f,0.00459248583387911f,
                0.00467852169575331f,0.00476453932712372f,0.00485048718146969f,
                0.00493631296989803f,0.00502196481132434f,0.00510738811080472f,
                0.00519253019665306f,0.00527733603382653f,0.00536175130911293f,
                0.00544572077623164f,0.00552918940111833f,0.00561210143632972f,
                0.00569440156022116f,0.00577603365418253f,0.00585694239722419f,
                0.00593707151248598f,0.00601636576439999f,0.00609476917567206f,
                0.00617222670918391f,0.00624868291096363f,0.00632408337396158f,
                0.00639837317357751f,0.00647149894201256f,0.00654340656084022f,
                0.00661404368509652f,0.0066833574472246f,0.0067512967638352f,
                0.00681781004936821f,0.00688284794124018f,0.00694636024263923f,
                0.00700830006852264f,0.00706861799747793f,0.00712727022872542f,
                0.00718420968373347f,0.00723939236320011f,0.00729277614483476f,
                0.00734431903450479f,0.0073939801162596f,0.00744172079761816f,
                0.00748750334076135f,0.00753129165877846f,0.00757305048422015f,
                0.00761274702949899f,0.00765034936370192f,0.00768582725438369f,
                0.00771915204340711f,0.0077502982721315f,0.00777923898752073f,
                0.00780595238221188f,0.00783041576118145f,0.00785260990389047f,
                0.00787251633833038f,0.00789011932074644f,0.00790540415984307f,
                0.00791835882529146f,0.00792897231802682f,0.00793723650688416f,
                0.00794314411138689f,0.0079466907727103f,0.00794787320971588f,
                0.0079466907727103f,0.00794314411138689f,0.00793723650688416f,
                0.00792897231802682f,0.00791835882529146f,0.00790540415984307f,
                0.00789011932074644f,0.00787251633833038f,0.00785260990389047f,
                0.00783041576118145f,0.00780595238221188f,0.00777923898752073f,
                0.0077502982721315f,0.00771915204340711f,0.00768582725438369f,
                0.00765034936370192f,0.00761274702949899f,0.00757305048422015f,
                0.00753129165877846f,0.00748750334076135f,0.00744172079761816f,
                0.0073939801162596f,0.00734431903450479f,0.00729277614483476f,
                0.00723939236320011f,0.00718420968373347f,0.00712727022872542f,
                0.00706861799747793f,0.00700830006852264f,0.00694636024263923f,
                0.00688284794124018f,0.00681781004936821f,0.0067512967638352f,
                0.0066833574472246f,0.00661404368509652f,0.00654340656084022f,
                0.00647149894201256f,0.00639837317357751f,0.00632408337396158f,
                0.00624868291096363f,0.00617222670918391f,0.00609476917567206f,
                0.00601636576439999f,0.00593707151248598f,0.00585694239722419f,
                0.00577603365418253f,0.00569440156022116f,0.00561210143632972f,
                0.00552918940111833f,0.00544572077623164f,0.00536175130911293f,
                0.00527733603382653f,0.00519253019665306f,0.00510738811080472f,
                0.00502196481132434f,0.00493631296989803f,0.00485048718146969f,
                0.00476453932712372f,0.00467852169575331f,0.00459248583387911f,
                0.00450648293862937f,0.00442056298988981f,0.0043347756819607f,
                0.00424916957015157f,0.00416379190638012f,0.00407868915843243f,
                0.00399390727716584f,0.00390949298701744f,0.00382548754929838f,
                0.0037419341099305f,0.00365887687000188f,0.00357635304387541f,
                0.00349440485207333f,0.00341306869342068f,0.00333238315202735f,
                0.00325238301385568f,0.00317310402903081f,0.00309457881627437f,
                0.00301684037202565f,0.00293991879427246f,0.00286384456068003f,
                0.00278864525421695f,0.00271434853135328f,0.00264097966862761f,
                0.00256856350415117f,0.00249712273008277f,0.00242667960338182f,
                0.00235725407108704f,0.00228886578435181f,0.00222153210180776f,
                0.0021552698957957f,0.00209009409920761f,0.00202601882095278f,
                0.0019630565946842f,0.00190121885598122f,0.00184051553627144f,
                0.00178095593211534f,0.00172254686802054f,0.00166529545905812f,
                0.0016092064604517f,0.00155428377809904f,0.00150053049787485f,
                0.00144794847549755f,0.00139653874997478f,0.00134630073961758f,
                0.00129723349634724f,0.00124933294849455f,0.00120259568667747f,
                0.00115701579035543f,0.00111259467879384f,0.00106931822540122f,
                0.00102718116267983f,0.000986178938186383f,0.000946297770988519f,
                0.000907531736133415f,0.000869867550163783f,0.000833296223627447f,
                0.000797803936392077f,0.000763379735305123f,0.000730008986456393f,
                0.000697679145994846f,0.000666374509056456f,0.000636081420346253f,
                0.000606783353701641f,0.00057846533074219f,0.000551110329713203f,
                0.000524702286686557f,0.000499223470787463f,0.000474657120901507f,
                0.000450984809951676f,0.000428189160082477f,0.000406251324734714f,
                0.000385153271881389f,0.000364875996846684f,0.000345400869926808f,
                0.000326708879290118f,0.000308780932649348f,0.000291597869938214f,
                0.000275140684189231f,0.000259389796947092f,0.000244326216071779f,
                0.000229930935970451f,0.000216184332963057f,0.000203068219724462f,
                0.000190563098352905f,0.000178651614255598f,0.000167313729781744f,
                0.000156532956006339f,0.000146286563264041f,0.000136557693632332f,
                0.000127318515832955f,0.00011858488271745f,0.000110305105582749f,
                0.000102472352648184f,9.50761189756892e-05f,8.80908920993511e-05f,
                8.15069953090302e-05f,7.53030022037011e-05f,6.94677392105823e-05f,
                6.39817111679586e-05f,5.88334659753868e-05f,5.40049942485039e-05f,
                4.94850355640381e-05f,4.52564995216614e-05f,4.13087235793786e-05f,
                3.76257103637768e-05f,3.41970127627159e-05f,3.10078416300241e-05f,
                2.80482069458485e-05f,2.53041217751702e-05f,2.27663570560532e-05f,
                2.04216473604631e-05f,1.82615229911579e-05f,1.62735666868895e-05f,
                1.44499784426458e-05f,1.27793254538403e-05f,1.12544303321693e-05f,
                9.86490402815922e-06f,8.60429217413407e-06f,7.46302035608228e-06f,
                6.43585100707017e-06f,5.51383082956111e-06f,4.69288416706982e-06f,
                3.9654510777885e-06f,3.32837274322472e-06f,2.77554917169069e-06f,
                2.30553271878356e-06f,1.91395978260434e-06f,1.60141328600742e-06f,
                1.36612292422125e-06f,1.21185270930909e-06f,1.14053084753113e-06f,
                -9.5752605466603e-06f};
        */
        firFilterCompass[0] = new FIRFilter();
        firFilterCompass[0].init(parameter_fir_filter);
        firFilterCompass[1] = new FIRFilter();
        firFilterCompass[1].init(parameter_fir_filter);
        firFilterCompass[2] = new FIRFilter();
        firFilterCompass[2].init(parameter_fir_filter);
        firFilterAcc[0] = new FIRFilter();
        firFilterAcc[0].init(parameter_fir_filter);
        firFilterAcc[1] = new FIRFilter();
        firFilterAcc[1].init(parameter_fir_filter);
        firFilterAcc[2] = new FIRFilter();
        firFilterAcc[2].init(parameter_fir_filter);
        Log.i("MYDEBUG","D");

        offset_acc = new Float[3];
        offset_acc[0] = 0.0f;
        offset_acc[1] = 0.0f;
        offset_acc[2] = 0.0f;

        value_acc = new Float[3];
        value_acc[0] = 0.0f;
        value_acc[1] = 0.0f;
        value_acc[2] = 0.0f;

        old_value_acc = new Float[3];
        old_value_acc[0] = 0.0f;
        old_value_acc[1] = 0.0f;
        old_value_acc[2] = 0.0f;

        max_value_calibration_acc = new Float[3];
        max_value_calibration_acc[0] = 0.0f;
        max_value_calibration_acc[1] = 0.0f;
        max_value_calibration_acc[2] = 0.0f;

        min_value_calibration_acc = new Float[3];
        min_value_calibration_acc[0] = 0.0f;
        min_value_calibration_acc[1] = 0.0f;
        min_value_calibration_acc[2] = 0.0f;

        flag_busy_calibration_acc = false;
        number_sample_calibration_acc = 0;
        limit_sample_calibration_acc = 500;
        time_calibration_acc = 0.0f;

        Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Float[] data = (Float[]) msg.obj;
                if(data[0] == 1.0f) {
                    Float[] m_data = new Float[3];
                    m_data[0] = firFilterAcc[0].set(data[1]);
                    m_data[1] = firFilterAcc[1].set(data[2]);
                    m_data[2] = firFilterAcc[2].set(data[3]);
                    TextView textView = (TextView)findViewById(R.id.textViewFAccX);
                    textView.setText(Float.toString(m_data[0]));
                    textView = (TextView)findViewById(R.id.textViewFAccY);
                    textView.setText(Float.toString(m_data[1]));
                    textView = (TextView)findViewById(R.id.textViewFAccZ);
                    textView.setText(Float.toString(m_data[2]));
                    textView = (TextView)findViewById(R.id.textViewAccX);
                    textView.setText(Float.toString(data[1]));
                    textView = (TextView)findViewById(R.id.textViewAccY);
                    textView.setText(Float.toString(data[2]));
                    textView = (TextView)findViewById(R.id.textViewAccZ);
                    textView.setText(Float.toString(data[3]));
                    textView = (TextView)findViewById(R.id.textViewTimingAcc);
                    textView.setText(Float.toString(data[4]));
                    /*
                    if(flag_busy_calibration_acc == true) {
                        value_acc[0] = data[1];
                        value_acc[1] = data[2];
                        value_acc[2] = data[3];
                        float time = data[4];
                        if(number_sample_calibration_acc == limit_sample_calibration_acc) {
                            offset_acc[0] += time*(value_acc[0] + old_value_acc[0])/2.0f;
                            offset_acc[1] += time*(value_acc[1] + old_value_acc[1])/2.0f;
                            if(value_acc[0] > max_value_calibration_acc[0]) {
                                max_value_calibration_acc[0] = value_acc[0];
                            }
                            if(value_acc[1] > max_value_calibration_acc[1]) {
                                max_value_calibration_acc[1] = value_acc[1];
                            }
                            if(value_acc[0] < min_value_calibration_acc[0]) {
                                min_value_calibration_acc[0] = value_acc[0];
                            }
                            if(value_acc[1] < min_value_calibration_acc[1]) {
                                min_value_calibration_acc[1] = value_acc[1];
                            }
                            time_calibration_acc += time;
                            //Log.i("Debug", "Temp Offset acc : X - " + Float.toString(offset_acc[0]) + " | Y - " + Float.toString(offset_acc[1]));
                            offset_acc[0] = offset_acc[0]/time_calibration_acc;
                            offset_acc[1] = offset_acc[1]/time_calibration_acc;
                            max_value_calibration_acc[0] = offset_acc[0] + 2.0f*(max_value_calibration_acc[0] - offset_acc[0]);
                            max_value_calibration_acc[1] = offset_acc[1] + 2.0f*(max_value_calibration_acc[1] - offset_acc[1]);
                            min_value_calibration_acc[0] = offset_acc[0] + 2.0f*(min_value_calibration_acc[0] - offset_acc[0]);
                            min_value_calibration_acc[1] = offset_acc[1] + 2.0f*(min_value_calibration_acc[1] - offset_acc[1]);
                            Log.i("Debug", "Offset acc : X - " + Float.toString(offset_acc[0]) + " | Y - " + Float.toString(offset_acc[1]));
                            old_value_acc[0] = 0.0f;
                            old_value_acc[1] = 0.0f;
                            old_value_acc[2] = 0.0f;
                            number_sample_calibration_acc = 0;
                            flag_busy_calibration_acc = false;
                        } else if(number_sample_calibration_acc == 0) {
                            old_value_acc[0] = value_acc[0];
                            old_value_acc[1] = value_acc[1];
                            old_value_acc[2] = value_acc[2];
                            max_value_calibration_acc[0] = value_acc[0];
                            max_value_calibration_acc[1] = value_acc[1];
                            max_value_calibration_acc[2] = value_acc[2];
                            min_value_calibration_acc[0] = value_acc[0];
                            min_value_calibration_acc[1] = value_acc[1];
                            min_value_calibration_acc[2] = value_acc[2];
                            number_sample_calibration_acc++;
                        } else {
                            offset_acc[0] += time*(value_acc[0] + old_value_acc[0])/2.0f;
                            offset_acc[1] += time*(value_acc[1] + old_value_acc[1])/2.0f;
                            if(value_acc[0] > max_value_calibration_acc[0]) {
                                max_value_calibration_acc[0] = value_acc[0];
                            }
                            if(value_acc[1] > max_value_calibration_acc[1]) {
                                max_value_calibration_acc[1] = value_acc[1];
                            }
                            if(value_acc[0] < min_value_calibration_acc[0]) {
                                min_value_calibration_acc[0] = value_acc[0];
                            }
                            if(value_acc[1] < min_value_calibration_acc[1]) {
                                min_value_calibration_acc[1] = value_acc[1];
                            }
                            time_calibration_acc += time;
                            //Log.i("Debug", "Temp Offset acc : X - " + Float.toString(offset_acc[0]) + " | Y - " + Float.toString(offset_acc[1]));
                            old_value_acc[0] = value_acc[0];
                            old_value_acc[1] = value_acc[1];
                            old_value_acc[2] = value_acc[2];
                            number_sample_calibration_acc++;
                        }
                    } else {
                        if((data[1] > max_value_calibration_acc[0]) || (data[1] < min_value_calibration_acc[0])) {
                            value_acc[0] = data[1] - offset_acc[0];
                        } else {
                            value_acc[0] = 0.0f;
                        }
                        if((data[2] > max_value_calibration_acc[1]) || (data[2] < min_value_calibration_acc[1])) {
                            value_acc[1] = data[2] - offset_acc[1];
                        } else {
                            value_acc[1] = 0.0f;
                        }
                        //value_acc[1] = data[2] - offset_acc[1];
                        value_acc[2] = data[3] - offset_acc[2];
                        float time = data[4];
                        TextView textView = (TextView)findViewById(R.id.textViewAccX);
                        textView.setText(Float.toString(value_acc[0]));
                        textView = (TextView)findViewById(R.id.textViewAccY);
                        textView.setText(Float.toString(value_acc[1]));
                        textView = (TextView)findViewById(R.id.textViewAccZ);
                        textView.setText(Float.toString(value_acc[2]));
                        textView = (TextView)findViewById(R.id.textViewTimingAcc);
                        textView.setText(Float.toString(time));
                    }*/
                } else if(data[0] == 2.0f) {
                    Float[] m_data = new Float[3];
                    m_data[0] = firFilterCompass[0].set(data[1]);
                    m_data[1] = firFilterCompass[1].set(data[2]);
                    m_data[2] = firFilterCompass[2].set(data[3]);
                    TextView textView = (TextView)findViewById(R.id.textViewAngleCompassX);
                    textView.setText(Float.toString(m_data[0]));
                    textView = (TextView)findViewById(R.id.textViewAngleCompassY);
                    textView.setText(Float.toString(m_data[1]));
                    textView = (TextView)findViewById(R.id.textViewAngleCompassZ);
                    textView.setText(Float.toString(m_data[2]));
                    textView = (TextView)findViewById(R.id.textViewCompassX);
                    textView.setText(Float.toString(data[1]));
                    textView = (TextView)findViewById(R.id.textViewCompassY);
                    textView.setText(Float.toString(data[2]));
                    textView = (TextView)findViewById(R.id.textViewCompassZ);
                    textView.setText(Float.toString(data[3]));
                    textView = (TextView)findViewById(R.id.textViewTimingCompass);
                    textView.setText(Float.toString(data[4]));

                    if(flag_busy_calibration_compass == true) {
                        if(number_sample_calibration_compass < limit_sample_calibration_compass) {
                            value_calibration_compass_x[number_sample_calibration_compass] = data[1];
                            value_calibration_compass_y[number_sample_calibration_compass] = data[2];
                            value_calibration_compass_z[number_sample_calibration_compass] = data[3];
                            number_sample_calibration_compass++;
                        } else {
                            flag_busy_calibration_compass = false;
                            Float[] temp_data = new Float[3];
                            temp_data[0] = 0.0f;
                            temp_data[1] = 0.0f;
                            temp_data[2] = 0.0f;
                            for(int i = 0; i < number_sample_calibration_compass; i++) {
                                temp_data[0] += value_calibration_compass_x[i];
                                temp_data[1] += value_calibration_compass_y[i];
                                temp_data[2] += value_calibration_compass_z[i];
                            }
                            value_init_compass_x = temp_data[0]/(float)number_sample_calibration_compass;
                            value_init_compass_y = temp_data[1]/(float)number_sample_calibration_compass;
                            value_init_compass_z = temp_data[2]/(float)number_sample_calibration_compass;
                            textView = (TextView)findViewById(R.id.textViewAngleCompassX);
                            textView.setText(Float.toString(value_init_compass_x));
                            textView = (TextView)findViewById(R.id.textViewAngleCompassY);
                            textView.setText(Float.toString(value_init_compass_y));
                            textView = (TextView)findViewById(R.id.textViewAngleCompassZ);
                            textView.setText(Float.toString(value_init_compass_z));
                            value_calibration_compass_x = null;
                            value_calibration_compass_y = null;
                            value_calibration_compass_z = null;
                            flag_enable_run = true;
                        }
                    } else if(flag_enable_run == true) {
                        float angle_z = (float)Math.atan2(data[1], data[2]);
                        float temp = (float)Math.sqrt(data[1]*data[1] + data[2]*data[2]);
                        float angle_xy = (float)Math.atan2(data[3],temp);
                        textView = (TextView)findViewById(R.id.textViewAngleCompassX);
                        textView.setText(Float.toString(angle_z*180.0f/(float)Math.PI));
                        textView = (TextView)findViewById(R.id.textViewAngleCompassY);
                        textView.setText(Float.toString(angle_xy*180.0f/(float)Math.PI));
                    }
                }
            }
        };

        mySensorEventListener = new MySensorEventListener(mHandler);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //mGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mComp = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        Button mButtonCalibrationAcc = (Button)findViewById(R.id.button_calibration_acc);
        mButtonCalibrationAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag_busy_calibration_compass == false) {
                    flag_busy_calibration_compass = true;
                    number_sample_calibration_compass = 0;
                    limit_sample_calibration_compass = 20;
                    value_calibration_compass_x = null;
                    value_calibration_compass_y = null;
                    value_calibration_compass_z = null;
                    value_calibration_compass_x = new Float[limit_sample_calibration_compass];
                    value_calibration_compass_y = new Float[limit_sample_calibration_compass];
                    value_calibration_compass_z = new Float[limit_sample_calibration_compass];
                    for(int i = 0; i < limit_sample_calibration_compass; i++) {
                        value_calibration_compass_x[i] = 0.0f;
                        value_calibration_compass_y[i] = 0.0f;
                        value_calibration_compass_z[i] = 0.0f;
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mySensorEventListener, mAcc, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(mySensorEventListener, mComp, SensorManager.SENSOR_DELAY_FASTEST);
        //mSensorManager.registerListener(mySensorEventListener, mGyro, SensorManager.SENSOR_DELAY_FASTEST);

        //mSensorManager.registerListener(mySensorEventListener, mAcc, SensorManager.SENSOR_DELAY_NORMAL);
        //mSensorManager.registerListener(mySensorEventListener, mComp, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mySensorEventListener);
    }
}

class MyCompass {
    private Float[] angle;
    private Float[] init_value;
    private float error_value = 0.0f;
    private float k = 1.0f;

    public MyCompass() {
    }

    public void init(float data_1, float data_2, float data_3) {
        init_value = new Float[3];
        angle = new Float[3];
        float temp_1;
        temp_1 = (float)Math.sqrt(Math.pow(data_1,2.0) + Math.pow(data_2,2.0) + Math.pow(data_3,2.0));
        init_value[0] = data_1/temp_1;
        init_value[1] = data_2/temp_1;
        init_value[2] = data_3/temp_1;
        angle[0] = 0.0f;
        angle[1] = 0.0f;
        angle[2] = 0.0f;
    }

    public void input(float data_1, float data_2, float data_3) {
        Float[] temp_data = new Float[3];
        float temp_1;
        temp_1 = (float)Math.sqrt(Math.pow(data_1,2.0) + Math.pow(data_2,2.0) + Math.pow(data_3,2.0));
        temp_data[0] = data_1/temp_1;
        temp_data[1] = data_2/temp_1;
        temp_data[2] = data_3/temp_1;

        Float[] S = new Float[3];
        S[0] = (float)Math.sin(angle[0]);
        S[1] = (float)Math.sin(angle[1]);
        S[2] = (float)Math.sin(angle[2]);

        Float[] C = new Float[3];
        C[0] = (float)Math.cos(angle[0]);
        C[1] = (float)Math.cos(angle[1]);
        C[2] = (float)Math.cos(angle[2]);

        Float[] A = new Float[3];
        A[0] = temp_data[0] - init_value[0]*C[1]*C[2] - init_value[1]*C[1]*S[2] + init_value[2]*S[2];
        A[1] = temp_data[1] - init_value[0]*(S[0]*S[1]*C[2]-C[0]*S[2]) - init_value[1]*(S[0]*S[1]*S[2]+S[0]*C[2]) + init_value[2]*S[0]*C[1];
        A[2] = temp_data[2] - init_value[0]*(C[0]*S[1]*C[2]+S[0]*S[2]) - init_value[1]*(C[0]*S[1]*S[2]-S[0]*C[2]) + init_value[2]*C[0]*C[1];

        Float[] d_angle = new Float[3];
        d_angle[0] = -k*(A[1]*(-init_value[0]*C[0]*S[1]*C[2]-init_value[0]*S[0]*S[2]-init_value[1]*C[0]*S[1]*S[2]-init_value[1]*C[0]*C[2]+init_value[2]*C[0]*C[1]) + A[2]*(init_value[0]*S[0]*S[1]*C[2]-init_value[0]*C[0]*S[2]+init_value[1]*S[0]*S[1]*S[2]+init_value[1]*C[0]*C[2]-init_value[2]*S[0]*C[2]));
        d_angle[1] = -k*(A[0]*(init_value[0]*S[1]*C[2] + init_value[1]*S[1]*S[2] + init_value[2]*C[1]) + A[1]*(-init_value[0]*S[0]*C[1]*C[2] - init_value[1]*S[0]*C[1]*S[2] - init_value[2]*S[0]*S[1]) + A[2]*(-init_value[0]*C[0]*C[1]*C[2]-init_value[1]*C[0]*C[1]*S[2]-init_value[2]*C[0]*S[1]));
        d_angle[2] = -k*(A[0]*(init_value[0]*C[1]*S[2]-init_value[1]*C[1]*C[2]) + A[1]*(init_value[0]*S[0]*S[1]*S[2] + init_value[0]*C[0]*C[2] - init_value[1]*S[0]*S[1]*C[2] + init_value[1]*S[0]*S[2]) + A[2]*(init_value[0]*C[0]*C[1]*C[2] - init_value[0]*S[0]*C[2] - init_value[1]*C[0]*S[1]*C[2] - init_value[1]*S[0]*S[2]));

        angle[0] += d_angle[0];
        angle[1] += d_angle[1];
        angle[2] += d_angle[2];

        error_value = 0.5f*(A[0]*A[0]+A[1]*A[1]+A[2]*A[2]);
    }

    Float[] getRotAngle() {
        Float[] data_return = new Float[3];
        data_return[0] = 180.0f*angle[0]/(float)Math.PI;
        data_return[1] = 180.0f*angle[1]/(float)Math.PI;
        data_return[2] = 180.0f*angle[2]/(float)Math.PI;
        return data_return;
    }

    float getError() {
        return error_value;
    }
}

class MySensorEventListener implements SensorEventListener {

    private Handler mHandler;
    private long timing_acc, timing_gyro, timing_compass;

    public MySensorEventListener(Handler handler) {
        mHandler = handler;
        timing_acc = android.os.SystemClock.elapsedRealtimeNanos();
        timing_gyro = timing_acc;
        timing_compass = timing_acc;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            Message msg = mHandler.obtainMessage();
            Float[] data = new Float[5];
            data[0] = 1.0f;
            data[1] = event.values[0];
            data[2] = event.values[1];
            data[3] = event.values[2];
            long temp = android.os.SystemClock.elapsedRealtimeNanos();
            data[4] = ((float)(temp - timing_acc))/1000000000.0f;
            timing_acc = temp;
            msg.obj = data;
            mHandler.sendMessage(msg);
        } else if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            Message msg = mHandler.obtainMessage();
            Float[] data = new Float[5];
            data[0] = 2.0f;
            data[1] = event.values[0];
            data[2] = event.values[1];
            data[3] = event.values[2];
            long temp = android.os.SystemClock.elapsedRealtimeNanos();
            data[4] = ((float)(temp - timing_compass))/1000000000.0f;
            timing_compass = temp;
            msg.obj = data;
            mHandler.sendMessage(msg);
        } else if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            Message msg = mHandler.obtainMessage();
            Float[] data = new Float[5];
            data[0] = 3.0f;
            data[1] = event.values[0];
            data[2] = event.values[1];
            data[3] = event.values[2];
            long temp = System.nanoTime();
            data[4] = ((float)(temp - timing_gyro))/1000000000.0f;
            timing_gyro = temp;
            msg.obj = data;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}

class FIRFilter {
    Float[] H;
    Float[] store_input;
    int size;
    public FIRFilter() {
        //Log.i("MYDEBUG","C");
        H = null;
        store_input = null;
    }

    public void init(Float[] data) {
        //Log.i("MYDEBUG","B");
        size = data.length;
        H = new Float[size];
        H = data.clone();
        store_input = new Float[size];
        for(int i = 0; i < size; i++) {
            store_input[i] = 0.0f;
        }
    }

    public float set(float input) {
        //Log.i("MYDEBUG","A");
        float temp = 0;
        for(int i = size-1; i > 0; i--) {
            store_input[i] = store_input[i-1];
            temp += store_input[i]*H[i];
        }
        store_input[0] = input;
        temp += store_input[0]*H[0];
        return temp;
    }
}
