In order to correctly build solutions and answer all your questions
- it would be great to have more functional and non-functional requirements

There`s a question - how can I define that part is compatible or no?
Maybe answer would help me to build better or correct solution :)

If consistency is really important to us, then ->
First approach - use SQL db for data storage of the compatibility of parts

Example of hibernate code

@Entity
public class AutomobilePart {

    ...
    @Id
    private String serialNumber;

    private String manufacturer;

    private double weight;

    @ManyToMany
    private Set<AutomobilePart> parents = new HashSet<>();

    @ManyToMany(mappedBy = "parents")
    private Set<AutomobilePart> children = new HashSet<>();

    ...
}


Example of hibernate code - case if detail can be compatible one to many

@Entity
public class AutomobilePart {

    ...
    @Id
    private String serialNumber;

    private String manufacturer;

    private double weight;

    @ManyToOne(fetch = FetchType.LAZY)
    private AutomobilePart parentAutomobilePart;

    @OneToMany(mappedBy = "parentAutomobilePart")
    private Set<AutomobilePart> compatibleParts = new HashSet<>();

    ...
}

For this approach we would use Hibernate as ORM and SQL DB (e.g. PostgreSQL)

1) How is your solution impacted if the number of parts in the catalog reaches tens of
   millions?
Depends on how many user we will have in our platform - but in general this solution should more or less ok
DB indexes should be used for slow queries, Optimistic locking recommended to use

in case if performance is poor (and performance problem only in DB lvl, queries are ok, app level is ok):
We can use SQL db with better performance (if we are allowed to use cloud)
e.g. AWS Aurora can be used for that - comparing to Mysql - 5x performance is better
                                       comparing to PostgreSQL - 3x performance is better
We could try to use read replica - all write queries goes to one DB instance, all write queries goes to read replica
and async replication between both instances
advantages:
1) There are lots of advantages of using cloud solution for DB, we can discuss it

disadvantages:
1) Maintenance of cloud db requires specific knowledge
2) the price is a little higher
3) Not all projects are allowed to use cloud technologies

in case if performance is still poor:
We can have Elasticsearch and perform read queries by Elasticsearch and write queries by Hibernate
advantages:
1) performance of app generally should be ok for write and for read operations
2) Maintenance of relationship is taken by Hibernate

disadvantages:
1) Need to maintain Elasticsearch
2) usually refactoring should be done in case if most heavy read request will be performed by ES
3) How to insert/update data from SQL db to ES? (can be updated asynchronously or parsing db logs (WAL Advance Record Log), like debezium - eventual consistency as a result)

All solutions can be combined for better performance

Also cache systems can be used to reduce read activity from DB and increase performance

2) How does the solution change if new parts are added and old parts removed from the
   catalog at high frequency?
In case of high frequency we could use:
1) batching
2) in case we have too much write operations we can use queues in order to avoid failing DB instance
3) reactive (non-blocking) approach


Second approach is to use noSQL db
1) Graph database (like AWS Neptune) - almost no experience with this type of DB, but I would do investigation and research before making design
2) Document db can be used (like MongoDB) in order to store data in JSON
3) Key value db can be used (like AWS DynamoDb)

1) How is your solution impacted if the number of parts in the catalog reaches tens of millions?
Usually should not be impacted because one of NoSQL dbs benefits - high speed
NoSQL databases are suitable for storing large amounts of unstructured information

2) How does the solution change if new parts are added and old parts removed from the
   catalog at high frequency?
The decision would not have changed because it would have no impact. With NoSQL, we have to consider
CAP theorem, eventually consistency, etc. User could read an old data but in next second (with next request)
data can be updated and user would get consistence data
For better read performance caches can be used also (Redis, or DAX for DynamoDB)


To make a long story short:
Main question SQL or NoSQL - but it is difficult to answer without additional information (requirements)

NoSQL are needed if the data requirements are unclear, uncertain, and can change with the growth and development of the project.
 And also in cases where one of the main requirements for databases is high speed.
In the other hand SQL dbs are suitable for storing structured data, especially when consistency is critical.