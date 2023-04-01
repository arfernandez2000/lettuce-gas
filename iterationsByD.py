import matplotlib.pyplot as plt
import numpy as np
import sys


x = []
y = []
error = []
for i in range(1, len(sys.argv)):
    iter = []
    velocity = open(sys.argv[i], "r")
    next(velocity) #Don't need N
    x.append(int(next(velocity)))
    for line in velocity:
        iter.append(int(line))
    y.append(np.mean(iter))
    error.append(np.std(iter))

print(x)
print(y)
print(error)

fig, ax = plt.subplots()
ax.errorbar(x, y, error, fmt='o', linewidth=2, capsize=6, color= "#ba68c8")

plt.title("Iteraciones vs. Tamaño del Tabique")
plt.xlabel("Tamaño del Tabique")
plt.ylabel("Iteraciones")

plt.show()