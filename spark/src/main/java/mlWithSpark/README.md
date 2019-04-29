## Spark机器学习

支持的机器学习算法：
- 协调过滤：交替最小二乘法（ALS，alternating least squares）
- 聚类：
    - K-均值（K-means）
    - 高斯混合模型（GMM，Gaussian mixture model）
    - 幂迭代聚类（PIC，power iteration clustering）
    - 隐含狄利克雷分布（LDA，latent Dirichlet allocation）
    - 二分K-均值（bisecting K-means）是一种典型的层次聚类算法
    - 流式K-均值聚类（steaming K-means）
- 分类
    - 决策树（decision trees）。决策树及其集成算法（ensemble）
    - 朴素贝叶斯（naive Bayes）
    - 概率分类器（probability classifier）
    - logistic 回归用于二元（是否）判断，是广义线性模型（GLM，generalized linear models）的一种特例
    - 随机森林（random forest）算法通过集成多个决策树来确定决策边界
- 降维
    - 奇异值分解（SVD，singular value decomposition）
    - 主成分分析（PCA，principal component analysis）
- 频繁模式挖掘
    - FP-growth。FP 为frequent pattern（频繁模式）的缩写
    - 关联规则（association rule）
- PrefixSpan。这是一种序列模式挖掘算法。
- 评估指标（evaluation metrics）。spark.mllib 提供了一套指标，用于评估算法。
- PMML 模型输出。PMML（predictive model markup language，预测模型标记语言）是一种基于XML 的预测模型交换格式。它使得各个分析类应用能够描述并相互交换由机器学习算法生成的预测模型。
- 参数优化算法
    - 随机梯度下降法（SGD，stochastic gradient descent）
- Limited-Memory BFGS（L-BFGS）。这是一种优化算法,且属于准牛顿算法家族（Quasi-Newton methods）的一种
