Bilkul! Ab jabki aapka poora system (Kafka, Zookeeper, aur fraud-detector-app) bilkul sahi se chal raha hai, chaliye isko step-by-step manually test karte hain. Isse aapko poora confidence aa jayega ki fraud detection logic sahi kaam kar raha hai.

Hum 2 alag-alag terminal windows ka istemal karenge.

Terminal 1: Yahan hum alerts dekhenge (Consumer).

Terminal 2: Yahan se hum transactions bhejenge (Producer).

Step 1: Alerts Sunne ke liye Listener Set Karein (Terminal 1)

Sabse pehle, hum ek listener start karenge jo fraud.alerts topic ko sunega. Jaise hi koi fraud detect hoga, message yahan dikhega.

Ek naya terminal kholiye.

Ismein neeche di gayi command paste karke Enter dabaiye:

Generated bash
docker exec -it kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic fraud.alerts --from-beginning


docker exec -it kafka: Hum kafka container ke andar jaa rahe hain.

kafka-console-consumer: Yeh Kafka ka tool hai jo messages padhta hai.

--topic fraud.alerts: Hum fraud.alerts topic ko sun rahe hain.

Yeh command chalane ke baad, terminal ruk jayega aur messages ka intezar karega. Is terminal window ko aise hi khula chhod dein aur side mein rakhein. Yahi par humare fraud alerts dikhenge.

Step 2: Transactions Bhejne ke liye Sender Set Karein (Terminal 2)

Ab hum ek doosra terminal use karke transactions.raw topic par messages bhejenge.

Ek aur NAYA terminal kholiye. (Pehla wala waise hi chhod dein).

Is naye terminal mein, yeh command paste karke Enter dabaiye:

Generated bash
docker exec -it kafka kafka-console-producer --broker-list localhost:9092 --topic transactions.raw

Yeh command chalane ke baad, terminal ek > prompt par ruk jayega. Ab aap yahan par JSON messages type ya paste karke "Enter" daba sakte hain.

Ab hamara setup testing ke liye taiyar hai!

Step 3: Alag-Alag Scenarios Test Karna

Chaliye, ab alag-alag cases test karte hain. Har scenario ke baad, aapko Terminal 1 mein output check karna hai.

Scenario 1: Fraudulent Activity (Fraud Pakadna)

Goal: Ek hi user (user-123) ke liye 5 second ke andar 3 transactions bhejna.
Action: Neeche diye gaye 3 JSON messages ko jaldi-jaldi (5 second ke andar) ek-ek karke copy-paste karke Terminal 2 (producer wala) mein daalein. Har line ke baad Enter dabayein.

Generated json
{"transactionId":"tx-101","userId":"user-123","amount":15.0,"timestamp":1672531201000}
      

(Enter dabayein)

Generated json
{"transactionId":"tx-102","userId":"user-123","amount":200.0,"timestamp":1672531202000}
      

(Enter dabayein)

Generated json
{"transactionId":"tx-103","userId":"user-123","amount":50.0,"timestamp":1672531203000}
      

(Enter dabayein)

Expected Output (Aapke Terminal 1 - Consumer mein):

Ab apne Terminal 1 (consumer wali window) ko dekhein. Thodi der mein (kuch seconds mein), aapko wahan ek message dikhega.

Generated json
{"userId":"user-123","message":"Suspicious activity detected for user user-123: 3 transactions in 5 seconds.","windowStart":1672531200000,"windowEnd":1672531205000}
      

(Note: windowStart aur windowEnd ki values thodi alag ho sakti hain, lekin userId aur message same hona chahiye.)

✅ Result: Test PASSED! System ne fraud pakad liya.

Scenario 2: Normal Activity (Fraud Nahi Hai)

Goal: Ek user (user-456) ke liye sirf 2 transactions bhejna. Is par alert nahi aana chahiye.
Action: Apne Terminal 2 (producer) mein yeh 2 messages daalein:

Generated json
{"transactionId":"tx-201","userId":"user-456","amount":10.0,"timestamp":1672531301000}
      

(Enter dabayein)

Generated json
{"transactionId":"tx-202","userId":"user-456","amount":12.0,"timestamp":1672531302000}
      

(Enter dabayein)

Expected Output (Aapke Terminal 1 - Consumer mein):

Kuch bhi output nahi aana chahiye. Aapka consumer window shaant rahega.

✅ Result: Test PASSED! System ne sahi Faisla liya ki yeh fraud nahi hai.

Scenario 3: Different Users, High Volume (Fraud Nahi Hai)

Goal: Alag-alag users ke liye jaldi-jaldi transactions bhejna. Isse bhi alert nahi aana chahiye kyunki fraud per-user check ho raha hai.
Action: Apne Terminal 2 (producer) mein yeh messages daalein:

Generated json
{"transactionId":"tx-301","userId":"user-A","amount":5.0,"timestamp":1672531401000}
      

(Enter dabayein)

Generated json
{"transactionId":"tx-302","userId":"user-B","amount":50.0,"timestamp":1672531402000}
      

(Enter dabayein)

Generated json
{"transactionId":"tx-303","userId":"user-C","amount":500.0,"timestamp":1672531403000}
      

(Enter dabayein)

Expected Output (Aapke Terminal 1 - Consumer mein):

Fir se, kuch bhi output nahi aana chahiye. Kyunki har transaction ek alag user ka tha.

✅ Result: Test PASSED! System user-level aggregation sahi se kar raha hai.

Step 4: System ko Saaf Karna (Cleanup)

Jab aapki testing poori ho jaye, to saare containers ko band karna zaroori hai.

Apne dono terminal windows (Producer aur Consumer) ko CTRL + C dabakar band kar dein.

Ek naya terminal kholiye (ya purane mein se koi ek use karein) aur project folder mein jaakar yeh command chalayein:

Generated bash
docker-compose down -v

down: Yeh saare containers ko stop aur remove kar dega.

-v: Yeh Kafka ke data (volumes) ko bhi delete kar dega, taaki agli baar aap shuru se shuru kar sakein.

Congratulations! Aapne apne Real-Time Fraud Detection system ko manually poori tarah se test kar liya hai.