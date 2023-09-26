package cn.fancychuan.spark3.sparkcore.framework.controller

import cn.fancychuan.spark3.sparkcore.framework.common.TController
import cn.fancychuan.spark3.sparkcore.framework.service.WordCountService


/**
  * 控制层
  */
class WordCountController extends TController {

    private val wordCountService = new WordCountService()

    // 调度
    def dispatch(): Unit = {
        // TODO 执行业务操作
        val array = wordCountService.dataAnalysis()
        array.foreach(println)
    }
}
