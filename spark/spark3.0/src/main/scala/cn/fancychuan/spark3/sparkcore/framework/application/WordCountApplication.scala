package cn.fancychuan.spark3.sparkcore.framework.application

import cn.fancychuan.spark3.sparkcore.framework.common.TApplication
import cn.fancychuan.spark3.sparkcore.framework.controller.WordCountController


object WordCountApplication extends App with TApplication{

    // 启动应用程序
    start(){
        val controller = new WordCountController()
        controller.dispatch()
    }

}
