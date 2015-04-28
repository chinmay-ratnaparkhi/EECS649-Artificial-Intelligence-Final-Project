; ----------------------------------------------------------------------------
;     Chinmay Ratnaparkhi
;     KUID : 2736356
;     EECS 649 - Project 01
;     Dr. Frank Brown
;     02/10/2015
; ----------------------------------------------------------------------------
#lang racket
(require data/heap)
; ----------------------------------------------------------------------------
;   Definition of the nodes used structure
; ----------------------------------------------------------------------------
(struct node 
  (action resulting_state predecessor g h f)
)
(define (node_build in_node [ls '()])
  (cond 
    [(eq? (node-predecessor in_node) 'none) 
     (cons 
      (list (node-action in_node) 
            (node-resulting_state in_node) 
            (node-g in_node) 
            (node-h in_node) 
            (node-f in_node)
       ) ls)]
     [else  
      (node_build (node-predecessor in_node) 
             (cons  
              (list 
               (node-action in_node)
               (node-resulting_state in_node)
               (node-g in_node)
               (node-h in_node)
               (node-f in_node)) 
              ls ))]
   )
 )
; ----------------------------------------------------------------------------
;   nodes used in the heap
; ----------------------------------------------------------------------------
(define 
  (heapItem= item1 item2)
    (<= (node-f item1)
        (node-f item2)
    )
 )
; ----------------------------------------------------------------------------
; Recursively searches for a given item in the given list. If not found,
; returns -1, if found returns the index at which it is located.
; ----------------------------------------------------------------------------
(define (finditem itemlist item) 
  (let templist ((itemlist itemlist) (i 0))
    (cond [(empty? itemlist) -1]
          [(eq? (car itemlist) item) i]
          [else (templist (cdr itemlist) (+ 1 i))]        
    )
  )
)
; ----------------------------------------------------------------------------
; For a particular given state calculates the coordinates
; as a (Y X) two-tuple.
; ----------------------------------------------------------------------------
(define (get_coord current_state tile)
  (define req (flatten current_state))
  (define cur (finditem req tile))
  (list 
   (floor (/ cur 3)) 
   (modulo cur 3)
  )
)
; ----------------------------------------------------------------------------
; Checks validity of the given two tuple of coordinates. Coordinates need to be 
; strictly greater than 0.
; ----------------------------------------------------------------------------
(define (coordinate_validity two_tuple)
  (cond [ (or (> (car two_tuple) 2) (< (car two_tuple) 0)) #f ]
        [(or (> (second two_tuple) 2) (< (second two_tuple) 0)) #f]
        [else #t]        
  )
)
; ----------------------------------------------------------------------------
; Given a two tuple of coordinates (Y, X), returns a singleton index.
; ----------------------------------------------------------------------------
(define (get_index two_tuple)
  (+ (* (car two_tuple) 3) (second two_tuple))
)
; ----------------------------------------------------------------------------
;   Returns the next possible location where a tile could be moved.
;   The possible coordinates are filtered and stored in a list called "valid"
;   i.e. valid location coordinates are listed returned.
; ----------------------------------------------------------------------------
(define (get_possible_coord two_tuple)
  ; X Coordinate is the second element of the pair
  (define x (second two_tuple))
  ; Y Coordinate is the first element of the pair
  (define y (car two_tuple)) 
  ; X Coordinate of the next state will be current X + 1 
  (define xn (+ x 1))
  ; Y Coordinate of the next state will be current Y + 1
  (define yn (+ y 1))
  ; X Coordinate of the previous state will be current X - 1
  (define xp (- x 1))
  ; Y Coordinate of the previous state will be current Y - 1
  (define yp (- y 1))
  ; Blank (0) moves up, the tile moves down, the tile moves up
  ; if (0) moves down, the tile moves up and so on. 
  (define umove (list yp x 'd))
  (define dmove (list yn x 'u))
  (define lmove (list y xp 'r))  
  (define rmove (list y xn 'l))
  (define valid (list umove dmove lmove rmove))
  (filter coordinate_validity valid) 
)
; ----------------------------------------------------------------------------
;   Switches two tiles. i.e. coordinates are switchped. 
;   Returns a new state in form of a list.
; ----------------------------------------------------------------------------
(define (switch input_state first second)
  (define flat (flatten input_state))
  (define elementIndex1 (get_index first))
  (define elementIndex2 (get_index second))
  (define element1 (list-ref flat elementIndex1))
  (define element2 (list-ref flat elementIndex2))
  
  (define new_state (map (lambda(x)
                        
                        (cond [(eq? x element1) element2]
                              [(eq? x element2) element1]
                              [else x]
                         )
                       ) 
                     flat
                 )
  )
  (list (take new_state 3) (take (cdddr new_state) 3) (take (cdddr (cdddr new_state)) 3))
)
; ----------------------------------------------------------------------------
;   Definition of the MOVE function
; ----------------------------------------------------------------------------
(define (move state)
  (define coordinates (get_coord state 0))
  (define position (get_possible_coord coordinates))
  (map (lambda(x)  
         (list (switch state x coordinates) 
               (list   
               (get_index x) (third x)) 1)
         ) 
       position
   )
 )
; ----------------------------------------------------------------------------
;  Manhattan Distance calculator for a given pair
; ----------------------------------------------------------------------------
(define (manhattan-calculator coordinate1 coordinate2)
  ( + (abs 
       (- (car coordinate1) (car coordinate2))
      )
      (abs 
       (- (second coordinate1) (second coordinate2))
      )
  )
)
; --------------------------------------------------------------------------------
;  Find the total sum of manhattan distances by calling the manhattan-calculator
;  in loop.
; --------------------------------------------------------------------------------
(define (total_manhattan state goal)
 ; (define gflat (flatten goal))
  (define flat (flatten state))
  (define z (get_coord state 0))
  (define total (map (lambda(x) 
                       (if (not(eq? x 0)) 
                           (manhattan-calculator (get_coord state x) 
                                                 (get_coord goal x)) 0
                       )
                     )flat))
  (apply + total)
)
; ----------------------------------------------------------------------------
;  Checks if the provided state is the same as the required state
; ----------------------------------------------------------------------------
(define (goal? provided_state desired_state )
  (equal? provided_state desired_state)
)
; ----------------------------------------------------------------------------
;  Null Heuristic
; ----------------------------------------------------------------------------
(define (nHeuristic state goal) 0)
; ----------------------------------------------------------------------------
;  Definition and implementation of the A* algorithm
;
; ----------------------------------------------------------------------------
(define 
  (AstartSearch start_state goal_state goal? moves heuristic)
  ; If the node has already been looked at
  (define visited (make-hash))
  (define frontier (make-heap (lambda (a b) (heapItem= a b))))
  (heap-add! frontier 
             (node 'START start_state 'none 0 
                   (heuristic start_state goal_state) 
                   (+ (heuristic start_state goal_state) 0))
             )
  (define counter 0)
  (define (keepdoing)
    (define front 
     (heap-min frontier)
    )
    (heap-remove-min! frontier)
    (cond 
      [(goal? goal-state (node-resulting_state front))
       ((lambda() 
          (displayln counter) front))
       ]
      [(empty? frontier) 
        'There_is_no_solution]
      [else 
        (hash-set! visited (node-resulting_state front) front)
        (set! counter (+ counter 1))
        (define pmoves (moves (node-resulting_state front)))
        (map
          (lambda(x)
             (hash-ref visited (first x) 
                (lambda()
                  (define nnode (node (second x) (first x) front (+ (node-g front) (third x)) 
                                      (heuristic (first x) goal_state)  
                                      ( + (heuristic (first x) goal_state) (+ (node-g front) (third x)))
                                 )
                  )  
              (heap-add! frontier nnode))
             )
          )pmoves
        )(keepdoing)
      ]
    )  
  )
  (keepdoing)
)
; ----------------------------------------------------------------------------
;   Goal State - Final desired result
; ----------------------------------------------------------------------------
(define goal-state '(
                     (1 2 3)
                     (4 5 6)
                     (7 8 0)
                    )
 )
; ----------------------------------------------------------------------------
;  Test cases 
; ----------------------------------------------------------------------------
(define test_puzzle1 '((6 4 2)
                       (1 5 3)
                       (7 0 8)) )

(define test_puzzle2 '((6 4 2)
                       (8 5 3)
                       (1 0 7)) )

(define test_puzzle3 '((8 0 7)
                       (6 5 4)
                       (3 2 1)) )

(define test_puzzle4 '((6 4 7)
                       (8 5 0)
                       (3 2 1)) )

(define test_puzzle5 '((1 2 3)
                       (4 5 6)
                       (8 7 0)) )
; ----------------------------------------------------------------------------
;  End
; ----------------------------------------------------------------------------

; ----------------------------------------------------------------------------
;  Methods to solve the 8-tile puzzle given a start state
; ----------------------------------------------------------------------------
; efficient solution
(define  (solve start end)
  (define nnode (time (AstartSearch start end goal? move total_manhattan)))
  (node_build nnode)
)
; Null heuristic solution
(define  (solve-null-heur start end)
  (define nnode (time (AstartSearch start end goal? move nHeuristic)))  
  (node_build nnode)
)
; ----------------------------------------------------------------------------
;  Solve the test cases
; ----------------------------------------------------------------------------
(displayln " --- --- -- Using manhattan Distance -- --- ---")
(solve test_puzzle1 goal-state)
(solve test_puzzle2 goal-state)
(solve test_puzzle3 goal-state)
(solve test_puzzle4 goal-state)

(displayln " --- --- -- Using Null Heuristic Function -- --- ---")
(solve-null-heur test_puzzle1 goal-state)
(solve-null-heur test_puzzle2 goal-state)
(solve-null-heur test_puzzle3 goal-state)
(solve-null-heur test_puzzle4 goal-state)

(displayln "Puzzle 5 -- Not working.") 
(solve test_puzzle5 goal-state)
(solve-null-heur test_puzzle5 goal-state)