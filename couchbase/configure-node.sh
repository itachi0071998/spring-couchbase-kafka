#!/usr/bin/env bash
set -m

/entrypoint.sh couchbase-server &

sleep 15

# Setup index and memory quota
curl -v -X POST http://localhost:8091/pools/default -d memoryQuota=512 -d indexMemoryQuota=512
# Setup services
curl -v http://localhost:8091/node/controller/setupServices -d services=kv%2cn1ql%2Cindex
# Setup credentials
curl -v http://127.0.0.1:8091/settings/web -d port=8091 -d username=minimumprice -d password=123456

#Create buckets
curl -v -u minimumprice:123456 -X POST http://localhost:8091/pools/default/buckets -d name=minimumprice -d bucketType=couchbase -d ramQuotaMB=256 -d authType=sasl -d enableFlush=1

curl -X POST -u 'minimumprice:123456' 'http://localhost:8091/settings/indexes' -d 'indexerThreads=1' -d 'logLevel=info' -d 'maxRollbackPoints=5' -d 'memorySnapshotInterval=200' -d 'stableSnapshotInterval=5000' -d 'storageMode=forestdb'

curl -v http://localhost:8091/query/service -d 'statement=CREATE PRIMARY INDEX ON minimumprice USING GSI;'

curl -v http://localhost:8091/query/service -d 'statement=CREATE INDEX gtinindex ON `minimumprice`(`gtin`) WHERE (`_class` = "com.tesco.demo.model.Price") USING GSI;'
echo "Type: $TYPE"

fg 1