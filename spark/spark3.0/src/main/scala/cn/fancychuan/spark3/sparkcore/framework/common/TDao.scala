package cn.fancychuan.spark3.sparkcore.framework.common

import cn.fancychuan.spark3.sparkcore.framework.util.EnvUtil

trait TDao {

    def readFile(path:String) = {
        EnvUtil.take().textFile(path)
    }
}
