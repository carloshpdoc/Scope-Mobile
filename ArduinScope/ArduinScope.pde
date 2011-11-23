char val;
char cen,dez,uni;
unsigned int pwm;
int sensorPin = A0;    // select the input pin for the potentiometer  
int sensorValue = 0;  // variable to store the value coming from the sensor
boolean stop=false;

void setup() {
  Serial.begin(9600);       // start serial communication at 9600bps
  pinMode(12, OUTPUT);  
  pinMode(13, OUTPUT); 
  pinMode(9, OUTPUT);   // sets the pin as output
  
}


void loop() {
  if( Serial.available() )       // if data is available to read
  {
    val = Serial.read();         // read it and store it in 'val'
    //Serial.println(val);
   // Serial.write("hello");
  if(val=='c'|| val=='C'){
    digitalWrite(12, HIGH);   // set the LED on
    digitalWrite(13,LOW);   // set the LED on  
  }else if(val=='a'||val=='A'){
    digitalWrite(13,HIGH);   // set the LED on  
    digitalWrite(12, LOW);   // set the LED on  
  }else if(val=='r'||val=='R'){
    //sensorValue = analogRead(sensorPin);  
   // Serial.print(sensorValue);
   cen = Serial.read(); 
   //Serial.println(cen);
   dez = Serial.read(); 
   //Serial.println(dez);
   uni = Serial.read(); 
   //Serial.println(uni);
   pwm = (short)(cen-'0')*100 + (short)(dez-'0')*10 + (short)(uni-'0');
   //Serial.print("V ");
   //Serial.println(pwm);
   analogWrite(9, pwm);           
  }  
  }
   sensorValue = analogRead(sensorPin); 
   delay(20);                    // wait 30ms for next reading
   //Serial.print('S');
   if(sensorValue<10){
     Serial.print("000");
   }else if(sensorValue<100){
     Serial.print("00"); 
   }else if(sensorValue<1000){
     Serial.print('0');
   }else{
   }
   Serial.println(sensorValue); 
   
}
