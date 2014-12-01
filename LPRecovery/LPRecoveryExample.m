% LPRecoveryExample.m
% EECS 393: Experimental evaluation of a conformity extraction system
% LPRecovery: minimize 

% 2 Linprog

% The linprog function has input parameters f, AA, b, Aeq, beq, lb, and ub.
% It has output parameters x where:
% minx (f^t * x) s.t. { AA.x<=b, Aeq.x = beq, lb <= x <= ub }
% f is a vector that specifies the coefficients of the objective function.
% AA is a matrix and b is a vector. Together, they specify the inequality constraints.
% Aeq is a matrix, and beq is a vector. Together, they specify the equality constraints.
% lb and ub are vectors that specify lower and upper bounds of unknown variables.

% 2.1 f Vector
% Find the coefficient of each unknown variable of x in order to place it into the vector f of linprog. 

% Example Adjacency matrix of graph G from report.
A = [
0 1 1 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 1 1;
1 0 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0;
1 1 0 1 0 0 0 1 1 1 0 0 0 0 0 0 0 0 0 0;
0 1 1 0 1 1 0 1 0 0 0 0 0 0 0 0 0 0 0 0;
0 0 0 1 0 1 1 1 0 1 0 0 0 0 0 0 0 0 0 0;
0 0 0 1 1 0 1 0 0 0 0 1 0 1 0 0 0 0 0 0;
0 0 0 0 1 1 0 0 0 0 1 1 0 0 0 0 0 0 0 0;
0 0 1 1 1 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0;
1 0 1 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 1 0;
0 0 1 0 1 0 0 0 1 0 1 0 0 0 0 1 0 0 1 0;
0 0 0 0 0 0 1 1 0 1 0 0 1 1 0 0 0 0 0 0;
0 0 0 0 0 1 1 0 0 0 0 0 0 1 0 0 0 0 0 0;
0 0 0 0 0 0 0 0 0 0 1 0 0 1 1 0 0 0 0 0;
0 0 0 0 0 1 0 0 0 0 1 1 1 0 1 1 0 0 0 0;
0 0 0 0 0 0 0 0 0 0 0 0 1 1 0 1 1 0 0 0;
0 0 0 0 0 0 0 0 0 1 0 0 0 1 1 0 1 1 1 0;
0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 1 0 1 0 0;
0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 1 0 1 1;
1 0 0 0 0 0 0 0 1 1 0 0 0 0 0 1 0 1 0 0;
1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0;]

% Expressed opinions for example graph.
ExpressedOp = [1 2 1 3 4 3 5 3 3 4 5 7 8 7 9 8 6 6 4 2]; % yi
ExpressedOp = ExpressedOp/10; % Data is normalized to [0,1].
lambda = [0.1, 0.2, 0.7];
c = -5;
bucketsdown = [0, 1/3, 2/3];
bucketsup = [1/3 - eps, 2/3 - eps, 1];

n = size(A, 2); % The number of nodes in A.
%Aeq = zeros(n*n+n, 2*n*n + 4*n);
d = ones(1, n); % This array contains the degrees of each node.
for i = 1:n % Calculate degrees.
    d(i) = sum(A(:, i) ~= 0);
end

M = diag(d)\(A*ExpressedOp'); % Calculate average opinions of neighbors.

Dist = graphallshortestpaths(sparse(A)); % Find all distances.

for i = 1:n % Change each distance to 1 - dij/n.
    for j = 1:n
        Dist(i, j) = 1 - Dist(i, j)/n;
    end
end

for i = 1:n % Eliminate the absolute value function.
    for j = 1:n
        Distt(2*(n*(i - 1) + j) - 1) = Dist(i, j);
        Distt(2*(n*(i - 1) + j)) = Dist(i, j);
        j = j + 1;
    end
end
for i = 1:n % Assign zero to zi coefficients.
    Distt(2*n*n + i) = 0;
end

% Beta ij Coefficients: log2(lambda)s re the coefficients of Beta ij's.
Betij = zeros(n, 3);
for i = 1:n
    Betij(i, :) = log2(lambda);
end
BB = zeros(1, 3*n);
for i = 1:n
    for j = 1:3
        BB((j - 1)*n + i) = c*Betij(i, j);
    end
end

f = [Distt BB]; % f vector is now ready to be used in linprog.

% 2.2 Matrix AA and Aeq

ym = ExpressedOp' - M; % yi - mi

% 2.2.1 First constraint (inequality)

for i = 1:n % First constraint in the paper for z's.
    AA(i, 2*n*n + i) = -1*ym(i);
end

% 2.2.2 Second constraint (inequality)

for i = 1:n % Second constraint in the paper for z's.
    for j = 1:3
        if ym(i) < 0
            AA(n*j + i, 2*n*n + i) = bucketsup(j);
        else
            AA(n*j + i, 2*n*n + i) = -1 * bucketsup(j);
        end
    end
end

for i = 1:n % Second constraint in the paper for Beta ij's.
    for j = 1:3
        AA(n*j + i, 2*n*n + j*n + i) = abs(ym(i));
    end
end

% 2.2.3 Third constraint (inequality) where K = 2/3

for i = 1:n % Third constraint for z's.
    for j = 1:3
        if ym(i) < 0
            AA(4*n + (n*(j - 1)) + i, 2*n*n + i) = -1 * bucketsdown(j);
        else
            AA(4*n + (n*(j - 1)) + i, 2*n*n + i) = bucketsdown(j);
        end
    end
end

for i = 1:n % Third constraint for Beta ij's.
    for j = 1:3
        AA(4*n + (n*(j - 1)) + i, 2*n*n + j*n + i) = 2/3 - abs(ym(i));
    end
end

% 2.2.4 Fourth constraint (equality)

for i = 1:n
    for j = 1:3
        Aeq(i, 2*n*n + j*n + i) = 1;
    end
end

% 2.2.5 Fifth constraint: Beta ij == [0,1] for all i,j - handled by upper and lower bound vectors

% 2.2.6 Additional constraint for newly introduced variables (equality)

for i = 1:n
    for j = 0:n
        if i ~= j
            Aeq(i*n + (j+1), 2*(n*(i - 1) + (j+1)) - 1) = 1;
            Aeq(i*n + (j+1), 2*(n*(i - 1) + (j+1))) = -1;
            Aeq(i*n + (j+1), 2*n*n + i) = -1;
            Aeq(i*n + (j+1), 2*n*n + (j+1)) = 1;
        end
    end
end
Aeq(n*n+n, :) = 0; % Bug: not enough rows in Aeq. Why?

% AA and Aeq matrices are ready.

% 2.3 For b and beq vectors:

for i = 1:n % First constraint (inequality).
    b(i) = -1 * M(i) * ym(i);
end

for j = 1:3 % Second constraint (inequality).
    for i = 1:n
        if ym(i) < 0
            b(j*n + i) = M(i)*bucketsup(j);
        else
            b(j*n + i) = -1*M(i)*bucketsup(j);
        end
    end
end

for j = 1:3 % Third constraint (inequality).
    for i = 1:n
        if ym(i) < 0
            b(4*n + (j - 1)*n + i) = -1*M(i)*bucketsdown(j) + 2/3;
        else
            b(4*n + (j - 1)*n + i) = M(i)*bucketsdown(j) + 2/3;
        end
    end
end

for i = 1:n % Fourth constraint (equality).
    beq(i) = 1;
end

% The fifth constraint is handled by lower and upper bound vectors.

for i = 1:n*n % Absolute value scores (equality constraint).
    beq(n + i) = 0;
end

% 2.4 Lower and upper bounds

lb = zeros(2*n*n + 4*n, 1); % Lower bound for all variable which is 0.
ub = ones(2*n*n + 4*n, 1); % Upper bound for all variable which is 1.

% 2.5 linprog call

[results, fval, EXITFLAG] = linprog(f, AA, b, Aeq, beq, lb, ub); 
% Recover the conformity parameters:

for i = 1:n
    conformity(i) = ym(i)/(results(2*n*n + i) - M(i));
end







            
           




