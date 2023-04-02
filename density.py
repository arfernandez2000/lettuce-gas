import matplotlib.pyplot as plt
import numpy as np
import sys

x = []
left = []
right = []

density = open(sys.argv[1], "r")
N = int(next(density))
epsilon = float(next(density))

i = 0
for line in density:
    if(i % 2 != 0):
        ll = line.split()
        left.append(int(ll[0]) / N * 100)
        right.append(int(ll[1]) / N * 100)
    else:
        x.append(int(line))
    i += 1

plt.xlim([0, np.max(x)])
plt.ylim([0, 100])

plt.plot(x, left, '-', color = "#ba68c8", label='Izquierda')
plt.plot(x, right, '-', color = "#d0d49c", label = "Derecha")
plt.hlines(y= 50 - (epsilon / 2) * 100, color='gray', xmin=0, xmax= np.max(x), linewidth= 1, label = "Corte de equilibrio")
plt.hlines(y= 50 + (epsilon / 2) * 100, color='gray', xmin=0, xmax= np.max(x), linewidth= 1)
plt.legend()

# aux = 50 - (0.1 / 2) * 100
# arr = np.array(right)
# print(np.argwhere(arr > aux)[0])


# plt.hlines(y= 50 - (0.1 / 2) * 100, color='gray', xmin=0, xmax= np.max(x), linewidth= 1, linestyles='dashed', label = "Corte de equilibrio")
# plt.hlines(y= 50 + (0.1 / 2) * 100, color='gray', xmin=0, xmax= np.max(x), linewidth= 1, linestyles='dashed')

# plt.vlines(x= np.argwhere(arr > aux)[0], color='gray', ymin=0, ymax= 50 - (0.1 / 2) * 100, linewidth= 1, linestyles='dashed')
# plt.legend()

plt.title("Iteraciones vs. Densidad de Partículas por Cámara")
plt.xlabel("Iteraciones")
plt.ylabel("Densidad de Partículas (%)")

plt.show()