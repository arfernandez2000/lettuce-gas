import math
import pygame

OFFSET_Y = 25.86751346

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
