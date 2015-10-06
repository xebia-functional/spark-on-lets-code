DROP KEYSPACE IF EXISTS #KEYSPACE#;

CREATE KEYSPACE #KEYSPACE# WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 1};

CREATE TABLE #KEYSPACE#.streaming_tweets_by_day (
	id text,
	user_id text,
	user_name text,
	user_screen_name text,
	created_timestamp text,
	created_day text,
	tweet_text text,
	lang text,
	retweet_count int,
	favorite_count int,
	latitude double,
	longitude double,
    PRIMARY KEY(created_day, id));

CREATE TABLE #KEYSPACE#.streaming_tweets_by_track (
	track text,
	year int,
	month int,
	day int,
	hour int,
	minute int,
	count counter,
    PRIMARY KEY(track, year, month, day, hour, minute)) 
    WITH CLUSTERING ORDER BY (year DESC, month DESC, day DESC, hour DESC, minute DESC);
