## HBase重要配置
- 关于StoreFile的合并
```
    <!-- 一个region进行 major compaction合并的周期,在这个点的时候， 这个region下的所有hfile会进行合并,默认是7天,major   
        compaction非常耗资源,建议生产关闭(设置为0)，在应用空闲时间手动触发 -->  
    <property>  
        <name>hbase.hregion.majorcompaction</name>  
        <value>604800000</value>  
        <description>The time (in miliseconds) between 'major' compactions of  
            all  
            HStoreFiles in a region. Default: Set to 7 days. Major compactions tend to  
            happen exactly when you need them least so enable them such that they  
            run at  
            off-peak for your deploy; or, since this setting is on a periodicity that is  
            unlikely to match your loading, run the compactions via an external  
            invocation out of a cron job or some such.  
        </description>  
    </property>
    <!-- 一个store里面允许存的hfile的个数，超过这个个数会被写到新的一个hfile里面 也即是每个region的每个列族对应的memstore在fulsh为hfile的时候，默认情况下当超过3个hfile的时候就会   
        对这些文件进行合并重写为一个新文件，设置个数越大可以减少触发合并的时间，但是每次合并的时间就会越长 -->  
    <property>  
        <name>hbase.hstore.compactionThreshold</name>  
        <value>3</value>  
        <description>  
            If more than this number of HStoreFiles in any one HStore  
            (one HStoreFile is written per flush of memstore) then a compaction  
            is run to rewrite all HStoreFiles files as one. Larger numbers  
            put off compaction but when it runs, it takes longer to complete.  
        </description>  
    </property>  
    <!-- 每个minor compaction操作的 允许的最大hfile文件上限 -->
    <property>  
        <name>hbase.hstore.compaction.max</name>  
        <value>10</value>  
        <description>Max number of HStoreFiles to compact per 'minor'  
            compaction.</description>  
    </property>  


    --------------------------------Flush--------------------------------
    <!-- regionServer的全局memstore的大小，超过该大小会触发flush到磁盘的操作,默认是堆大小的40%,而且regionserver级别的   
        flush会阻塞客户端读写 -->  
    <property>  
        <name>hbase.regionserver.global.memstore.size</name>  
        <value></value>  
        <description>Maximum size of all memstores in a region server before  
            new  
            updates are blocked and flushes are forced. Defaults to 40% of heap (0.4).  
            Updates are blocked and flushes are forced until size of all  
            memstores  
            in a region server hits  
            hbase.regionserver.global.memstore.size.lower.limit.  
            The default value in this configuration has been intentionally left  
            emtpy in order to  
            honor the old hbase.regionserver.global.memstore.upperLimit property if  
            present.  
        </description>  
    </property>  

    <!--可以理解为一个安全的设置，有时候集群的“写负载”非常高，写入量一直超过flush的量，这时，我们就希望memstore不要超过一定的安全设置。   
        在这种情况下，写操作就要被阻塞一直到memstore恢复到一个“可管理”的大小, 这个大小就是默认值是堆大小 * 0.4 * 0.95，也就是当regionserver级别   
        的flush操作发送后,会阻塞客户端写,一直阻塞到整个regionserver级别的memstore的大小为 堆大小 * 0.4 *0.95为止 -->  
    <property>  
        <name>hbase.regionserver.global.memstore.size.lower.limit</name>  
        <value></value>  
        <description>Maximum size of all memstores in a region server before  
            flushes are forced.  
            Defaults to 95% of hbase.regionserver.global.memstore.size (0.95).  
            A 100% value for this value causes the minimum possible flushing to  
            occur when updates are  
            blocked due to memstore limiting.  
            The default value in this configuration has been intentionally left  
            emtpy in order to  
            honor the old hbase.regionserver.global.memstore.lowerLimit property if  
            present.  
        </description>  
    </property>

        <!-- 内存中的文件在自动刷新之前能够存活的最长时间，默认是1h -->  
    <property>  
        <name>hbase.regionserver.optionalcacheflushinterval</name>  
        <value>3600000</value>  
        <description>  
            Maximum amount of time an edit lives in memory before being automatically  
            flushed.  
            Default 1 hour. Set it to 0 to disable automatic flushing.  
        </description>  
    </property>  
     <!-- 单个region里memstore的缓存大小，超过那么整个HRegion就会flush,默认128M -->  
    <property>  
        <name>hbase.hregion.memstore.flush.size</name>  
        <value>134217728</value>  
        <description>  
            Memstore will be flushed to disk if size of the memstore  
            exceeds this number of bytes. Value is checked by a thread that runs  
            every hbase.server.thread.wakefrequency.  
        </description>  
    </property>  
```