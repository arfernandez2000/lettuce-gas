import math
import pygame

HEX_RADIUS = 50
# OFFSET_Y = 25.86751346
OFFSET_Y = HEX_RADIUS / 2 
# OFFSET_Y = 1
X_START_EVEN = HEX_RADIUS
X_START_ODD = HEX_RADIUS + HEX_RADIUS
Y_START = HEX_RADIUS    


def draw_regular_polygon(surface, color, vertex_count, radius, position, width=1):
    n, r = vertex_count, radius
    x, y = position
    pygame.draw.polygon(surface, color, [
        (x + (r) * math.sin(2 * math.pi * i / n ), y + (r) * (math.cos(2 * math.pi * i / n)))
        for i in range(n)
    ], width)

def draw_hexagon_row(surface, color, radius, initial_position, hexagon_amount):
    for i in range(hexagon_amount):
        draw_regular_polygon(surface, color, 6, radius, ((initial_position[0] + 1.76 *radius * i), initial_position[1])) 

def draw_main_hexagons(surface, color, radius, initial_position, cols, rows):
    draw_hexagon_row(surface, pygame.Color(186,0,0), radius, (initial_position[0], initial_position[1]), cols)
    for i in range(1,rows-1):
        if (i % 2 == 0):
            draw_hexagon_row(surface, color, radius, (initial_position[0], initial_position[1] + i * radius * 2 - OFFSET_Y * i), cols)
        else:
            draw_hexagon_row(surface, color, radius, (initial_position[0] + radius * 0.86, initial_position[1] + i * radius * 2 - OFFSET_Y * i ), cols)
    if ((rows-1) % 2 == 0):
        draw_hexagon_row(surface, pygame.Color(186,0,0), radius, (initial_position[0], initial_position[1] + (rows-1) * radius * 2 - OFFSET_Y * (rows-1)), cols)
    else:
        draw_hexagon_row(surface, pygame.Color(186,0,0), radius, (initial_position[0] + radius * 0.86, initial_position[1] + (rows-1) * radius * 2 - OFFSET_Y * (rows-1)), cols)

def draw_solid_hexagon(surface, color, radius, x, y):
    if (y % 2 == 0):
        if (x % 2 == 0):
            draw_regular_polygon(surface, color, 6, radius, (radius + 2 * radius * x * 0.88, radius+ 2* 0.86 *radius * y - OFFSET_Y), width=0)
        else:
            draw_regular_polygon(surface, color, 6, radius, (radius + radius + 2 * radius * x * 0.88, radius), width=0)
    else:
        draw_regular_polygon(surface, color, 6, radius, (radius + 2 * radius * x * 0.88 + radius * 0.86, radius + 2 * radius * y - OFFSET_Y), width=0)


def draw_middle_wall(surface, color, radius, x, y):
    for i in range(y):
        draw_solid_hexagon(surface, color, radius, x, i)
# draw_middle_wall(screen, pygame.Color(255,255,0), HEX_RADIUS, 100)