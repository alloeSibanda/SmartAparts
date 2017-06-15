import com.pi4j.io.gpio.RaspiPin;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;



public class Main
{
    
    
     // Define the number of photos to take.
      private static final long _numberOfImages = 10;
      // Define the interval between photos.
      private static final int _delayInterval = 300;
       
      MqttPublish mqttpublish1 = new MqttPublish ();
      
      
      public static void main(String[] args) throws InterruptedException 
      {
          String topic1 = "smarthome/motion";
        String content1 = "Motion Detected in the house";
          String topic2 = "smarthome/panicbutton";
        String content2 = "Panic Button Pressed";
        int qos = 2;
        String broker = "tcp://localhost:1883";
        String clientId = "AlloePiMonitor";
        MemoryPersistence persistence = new MemoryPersistence();
         Lights HomeLights = new  Lights();
         
         //Light Sensor
       final LightDetector ld = new LightDetector(RaspiPin.GPIO_06, new LightDetectionInterface()
        {
           @Override
           public void lightDetected()
           {
             System.out.println(" night time switch on lights inside + outside");
             HomeLights.lightHome();
                        
         }
         
          @Override
           public void NolightDetected()
           {
            System.out.println(" during the day, no lights should be on ");
            HomeLights.lightAway();
           
         }
        
       });
       
       // Motion Sensor
        final MotionDetector md = new MotionDetector(RaspiPin.GPIO_04, new MotionDetectionInterface()
        {
           @Override
           public void motionDetected()
           {
             System.out.println(" motion detected");
            
             HomeLights.lightHome();
              try
                                 {
                                 //Create a new RaspiStill object.
                                   RaspiStill camera1 = new RaspiStill();
                                   // Loop through the number of images to take.
                                        for (long i = 0; i < _numberOfImages; ++i)
                                        {
                                           // Capture the image.
                                           camera1.TakePicture("image" + i + ".jpg",800,600);
                                              // Pause after each photo.
                                               Thread.sleep(_delayInterval);
                                         }
                                         }
                                       catch (Exception e)
                                           {
                                          // Exit the application with the exception's hash code.
                                            System.exit(e.hashCode());
                                               }  
              
                
       
         }
         
          @Override
           public void NomotionDetected()
           {
            System.out.println(" no motion detected ");
            HomeLights.lightAway();
           
         }
        
       });
       
       //Gas Sensor
        final GasDetector gd = new GasDetector(RaspiPin.GPIO_24, new GasDetectionInterface()
        {
           @Override
           public void GasAlert()
           {
             System.out.println(" gas / smoke detected, ring alarm ");
             HomeLights.lightHome();
             
                 try {
                 MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
                 MqttConnectOptions connOpts = new MqttConnectOptions();
                 connOpts.setCleanSession(true);
                 System.out.println("Connecting to broker: " + broker);
                 sampleClient.connect(connOpts);
                 System.out.println("Connected");
                 System.out.println("Publishing message: " + content2);
                 MqttMessage message = new MqttMessage(content2.getBytes());
                 message.setQos(qos);
                 sampleClient.publish(topic2, message);
                 System.out.println("Message published");
                 sampleClient.disconnect();
                 System.out.println("Disconnected");
                 //System.exit(0);
               } catch (MqttException me) {
                 System.out.println("reason " + me.getReasonCode());
                 System.out.println("msg " + me.getMessage());
                 System.out.println("loc " + me.getLocalizedMessage());
                 System.out.println("cause " + me.getCause());
                 System.out.println("excep " + me);
                 me.printStackTrace();
            }  
              
         }
         
          @Override
           public void NoGasAlert()
           {
            System.out.println(" no gas / smoke detected. Fresh Air ");
            HomeLights.lightAway();
           
         }
        
       });
       
       //Panic Button Listener
        final ButtonDetector bd = new ButtonDetector(RaspiPin.GPIO_02, new ButtonDetectionInterface()
        {
           @Override
           public void buttonDetected()
           {
             System.out.println(" panic button pressed, ring alarms");
             HomeLights.lightHome();
              
                                               
                try {
                 MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
                 MqttConnectOptions connOpts = new MqttConnectOptions();
                 connOpts.setCleanSession(true);
                 System.out.println("Connecting to broker: " + broker);
                 sampleClient.connect(connOpts);
                 System.out.println("Connected");
                 System.out.println("Publishing message: " + content2);
                 MqttMessage message = new MqttMessage(content2.getBytes());
                 message.setQos(qos);
                 sampleClient.publish(topic2, message);
                 System.out.println("Message published");
                 sampleClient.disconnect();
                 System.out.println("Disconnected");
                 //System.exit(0);
               } catch (MqttException me) {
                 System.out.println("reason " + me.getReasonCode());
                 System.out.println("msg " + me.getMessage());
                 System.out.println("loc " + me.getLocalizedMessage());
                 System.out.println("cause " + me.getCause());
                 System.out.println("excep " + me);
                 me.printStackTrace();
            }                                 
         }
         
          @Override
           public void NobuttonDetected()
           {
            System.out.println(" no panic button pressed ");
            HomeLights.lightAway();
           
         }
        
       });
       
       //Contacts Listener
        final ContactsDetector cd = new ContactsDetector(RaspiPin.GPIO_00, new ContactsDetectionInterface()
        {
           @Override
           public void contactsDetected()
           {
             System.out.println("Door and / or Window opened");
             HomeLights.lightHome();//add other methods I want;
              try
                                 {
                                 //Create a new RaspiStill object.
                                   RaspiStill camera1 = new RaspiStill();
                                   // Loop through the number of images to take.
                                        for (long i = 0; i < _numberOfImages; ++i)
                                        {
                                           // Capture the image.
                                           camera1.TakePicture("image" + i + ".jpg",800,600);
                                              // Pause after each photo.
                                               Thread.sleep(_delayInterval);
                                         }
                                         }
                                       catch (Exception e)
                                           {
                                          // Exit the application with the exception's hash code.
                                            System.exit(e.hashCode());
                                               }  
                                               
                 try {
                 MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
                 MqttConnectOptions connOpts = new MqttConnectOptions();
                 connOpts.setCleanSession(true);
                 System.out.println("Connecting to broker: " + broker);
                 sampleClient.connect(connOpts);
                 System.out.println("Connected");
                 System.out.println("Publishing message: " + content2);
                 MqttMessage message = new MqttMessage(content2.getBytes());
                 message.setQos(qos);
                 sampleClient.publish(topic2, message);
                 System.out.println("Message published");
                 sampleClient.disconnect();
                 System.out.println("Disconnected");
                 //System.exit(0);
               } catch (MqttException me) {
                 System.out.println("reason " + me.getReasonCode());
                 System.out.println("msg " + me.getMessage());
                 System.out.println("loc " + me.getLocalizedMessage());
                 System.out.println("cause " + me.getCause());
                 System.out.println("excep " + me);
                 me.printStackTrace();
            }  
         }
         
          @Override
           public void NocontactsDetected()
           {
            System.out.println(" Door and / or Window closed ");
            HomeLights.lightAway();
            //add other methods I want;
           
         }
        
       });
       
       //Sound Sensor
        final SoundDetector sd = new SoundDetector(RaspiPin.GPIO_29, new SoundDetectionInterface()
        {
           @Override
           public void soundDetected()
           {
             System.out.println(" sound detected,");
             HomeLights.lightHome();
             //add other methods I want;
              
         }
         
          @Override
           public void NosoundDetected()
           {
            System.out.println(" no sound detected ");
            HomeLights.lightAway();
            //add other methods I want;
           
         }
        
       });
       
       final Thread coreThread = Thread.currentThread();

        Runtime.getRuntime().addShutdownHook(new Thread()
       {
       

           //Runtime.getRuntime().addShutdownHook(new Thread()
       
           public void run()
            {
               System.out.println("\nUser interrupted.");
               synchronized (coreThread)
               {
                 coreThread.notify();
               }
            }
        });

        System.out.println("...On watch.");
        try 
        { 
      synchronized (coreThread)
      {
        coreThread.wait(); 
      }
    } 
    catch (Exception ex) { ex.printStackTrace(); }
    //ld.shutdown();
    System.out.println("Done.");
  }
}
