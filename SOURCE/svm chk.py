from sklearn import svm
X = [[0,0],[0,1]]
y = ["good","bad"]
clf = svm.SVC()
clf.fit(X,y)
print clf.predict([[2.,2.]])