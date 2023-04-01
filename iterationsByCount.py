import matplotlib.pyplot as plt
import numpy as np
import sys

print(len(sys.argv))
x = []
y = []
error = []
for i in range(1, len(sys.argv)):
    iter = []
    velocity = open(sys.argv[i], "r")
    x.append(int(next(velocity)))
    next(velocity) # Don't need D
    for line in velocity:
        iter.append(int(line))
    y.append(np.mean(iter))
    error.append(np.std(iter))

print(x)
print(y)
print(error)

fig, ax = plt.subplots()
ax.errorbar(x, y, error, fmt='o', linewidth=2, capsize=6, color= "#ba68c8")

plt.title("Iteraciones vs. Cantidad de Partículas")
plt.xlabel("Cantidad de Partículas")
plt.ylabel("Iteraciones")

plt.show()