import pandas
import pickle


class Data_Processing:

	def __init__(self,filename):
		self.csvfile = filename
		self.colnames = ['timestampMs','poorSignal','eegRawValue','eegRawValueVolts','attention','meditation','blinkStrength','delta','theta','alphaLow','alphaHigh','betaLow','betaHigh','gammaLow','gammaMid','tagEvent','location']
		self.data = pandas.read_csv(self.csvfile, names = self.colnames, header = 0)



	def Data_process(self,outputfile):

		
		alpha = self.data.alphaHigh.tolist()
		beta = self.data.betaHigh.tolist()
		gamma = self.data.gammaMid.tolist()
		delta = self.data.delta.tolist()
		theta = self.data.theta.tolist()


		minalpha = min(alpha)
		maxalpha = max(alpha)
		avgalpha = (minalpha+maxalpha)/2

		minbeta = min(beta)
		maxbeta = max(beta)
		avgbeta = (minbeta+maxbeta)/2

		mingamma = min(gamma)
		maxgamma = max(gamma)
		avggamma = (mingamma+maxgamma)/2

		mindelta = min(delta)
		maxdelta = max(delta)
		avgdelta = (mindelta+maxdelta)/2

		mintheta = min(theta)
		maxtheta = max(theta)
		avgtheta = (mintheta+maxtheta)/2

		#minvalues

		fo = open("Min.txt","a")	
		line = fo.write(str(minalpha)+","+str(minbeta)+","+str(mingamma)+","+str(mindelta)+","+str(mintheta)+"\n")
		fo.close()

		#avgvalues

		fo = open("Avg.txt","a")	
		line = fo.write(str(avgalpha)+","+str(avgbeta)+","+str(avggamma)+","+str(avgdelta)+","+str(avgtheta)+"\n")
		fo.close()


		#maxvalues

		fo = open("Max.txt","a")	
		line = fo.write(str(maxalpha)+","+str(maxbeta)+","+str(maxgamma)+","+str(maxdelta)+","+str(maxtheta)+"\n")
		fo.close()

		for i in range (299,len(delta)):

			fo = open(outputfile,"a")	
			line = fo.write(str(alpha[i])+","+str(beta[i])+","+str(gamma[i])+","+str(delta[i])+","+str(theta[i])+"\n")
			fo.close()

	def Data_transform(self,filename,outfile):
		setData = set()
		for linetopic in open(filename):
			setData.add(linetopic)
		
		for data in setData:

			fo = open(outfile,"a")	
			line = fo.write(data)
			fo.close()


		print len(setData)

	

	def Assign_labels(self,filename,maxfilename,trainingfile):
		for MaxData in open(maxfilename):
			MaxDataVal = MaxData.split(",")

		labelList = []
		trainingData = []

		label = ""
		fo = open(trainingfile,"a")	
		
			

		for waveData in open(filename):
			wavelist = []
			waveArr = waveData.split(',')
			for d in waveArr:
				wavelist.append(d.strip())

			Alpha = float(waveArr[0])/float(MaxDataVal[0])
			Beta = float(waveArr[1])/float(MaxDataVal[1])
			Gamma = float(waveArr[2])/float(MaxDataVal[2])
			Delta = float(waveArr[3])/float(MaxDataVal[3])
			Theta = float(waveArr[4])/float(MaxDataVal[4])

			if ((Alpha+Beta) >=1.5 or (Gamma+Delta+Theta)<=1):

				label = "stress"
			elif ((Alpha+Beta) <= 0.5 or (Gamma+Delta+Theta)>=2):
				label = "ADHD"
			else:
				label = "relaxed"

			labelList.append(label)
			trainingData.append(wavelist)

			waveData = waveData.strip()+","+label+"\n"
			line = fo.write(waveData)
		fo.close()

		print trainingData

		with open("TrainingData.out", "wb") as fp:
			pickle.dump(trainingData,fp)

		with open("LabelData.out","wb") as fp1:
			pickle.dump(labelList,fp1)






data_model = Data_Processing('eegIDRecord.csv')
#data_model.Data_process("DTfile.txt")
#data_model.Data_transform("DTfile.txt","UniqueData.txt")
data_model.Assign_labels("UniqueData.txt","Max.txt","training.txt")