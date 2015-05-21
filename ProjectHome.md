This project is my Master Thesis in Computer Engineering at the University of Liège (http://www.ulg.ac.be).

The latest version of the manuscipt as well as all the materials related to this work (videos, source codes, results databases,...) are available on this website.



**Design of intelligent agents for the soccer game**


Abstract:

> In today's computer science, machine learning and artificial intelligence are both very important subjects of research. Despite machines being more and more powerful, there are still plenty of problems that cannot be solved using analytical methods due to computational limitations. Moreover, their dynamics is sometimes unknown or too difficult to imitate. Reinforcement learning and dynamic programming algorithms provide an alternative way to tackle these problems.

> This master thesis first introduces the reader to RoboCup soccer simulation 2D. This famous football simulation platform provides a great environment for testing machine learning algorithms in large continuous state spaces. After explaining the main concepts behind this game, we then show how to start developing a new team. To do so, we detail the architecture of the java client we have created for this work: “Sebbot”. A few basic soccer strategies are implemented before we focus our attention on a case study: the ball capture. We solve this problem using two different learning algorithms. The first one, “Q-iteration”, is based on the computation of a so-called value function and uses a grid as function approximator. The second one is from the class of (direct) policy search algorithms and operates with cross-entropy optimization. Its use in this master thesis is a completely novel approach to the ball interception problem. The two methods are integrated in Sebbot and multiple videos are available to show their behavior. The last topic of this work is dedicated to the study of the various algorithm parameters and their impact on performances. A comprehensive set of benchmarks have been run and the numerical results are presented.