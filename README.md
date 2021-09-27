# phoenix-demo

I use this app to demonstrate replication between two clusters. This application performs the following:

- Gets the hostname
- Establishes connection to hbase via phoenix
- Drops or creates a table
- If keeping the table, queries the maximum value from previous runs and stores the return value as the new startkey (maxkey + 1)
- Enters a loop that will loop for the number of seconds specified as argument 1
- Writes a record with row format of hostname:::currentloopnumber, current loop number
- Queries for the last record written and prints the results to the command line

The idea is that you can start this on Cluster A and have it run for a good length of time. Then setup a replication policy to Cluster B. You will see the inital snapshot plus each new record appear in Cluster B. Then move the application to Cluster B and run it. You now have migrated your application, and are writing data to the new table as well as receiving new records from Cluster A. You can stop the application on Cluster A. You can query the data on Cluster B and note the differences in the hostname to validate receiving records from both application deployments.

The application takes two arguments:

- drop or keep : drops the table or keeps the table
- seconds : number of seconds to run

Sample:

 java -cp "/home/centos/helloPhoenix/*:/home/centos/hbase-conf-2:." app keep 10
 
21/09/26 22:01:05 [main] WARN impl.MetricsConfig: Cannot locate configuration: tried hadoop-metrics2-phoenix.properties,hadoop-metrics2.properties

START KEY: 300
 
WRITING TO TABLE APP

ROW KEY: ip-10-10-13-24.ec2.internal:::300 ROW VALUE: 300

...

FINISHED
