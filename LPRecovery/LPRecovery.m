
ExpressedOp = ExpressedOp/10; %data is normalized to [0,1]
lambda = [0.1, 0.2, 0.7];
c = -5;
bucketsdown= [0, 1/3, 2/3];
bucketsup=[1/3-eps,2/3-eps,1];


n = size(A,2); %The number of nodes in A
d = ones(1,n); %This array contains the degrees of each node.
for i =1:n %calculate degrees.
    d(i) = sum(A(:,i) ~= 0);
end

M = diag(d) \ (A * ExpressedOp'); %calculate average opinions of

Dist = graphallshortestpaths(sparse(A)); %find all distances.

for i=1:n % Change each distance to 1-dij/n
    for j=1:n
        Dist(i,j) = 1 - Dist(i,j)/n;
        
    end
end

for i =1:n %get rid of the absolute value function.
    for j=1:n
        Distt(2*(n*(i-1)+j)-1)=Dist(i,j);
        Distt(2*(n*(i-1)+j))=Dist(i,j);
        j=j+1;
    end
end

for i=1:n %assign zero to zi coefficients.
    Distt(2*n*n+i)=0;
end

Betij = zeros(n,3);
for i=1:n
    Betij(i,:) = log2(lambda);
end
BB = zeros(1,3*n);
for i = 1:n
    for j =1:3
        BB((j-1)*n+i) = c* Betij(i,j);
    end
end

f = [Distt BB]; % f vector is now ready to be used in linprog.

ym = ExpressedOp'-M; % yi-mi
for i=1:n %first constraint in the paper for z's.
    AA(i,2*n*n+i) = -1*ym(i);
end

for i=1:n %second constraint in the paper for z's
    for j=1:3
        if ym(i) < 0
            AA(n*j+ i,2*n*n+i) = bucketsup(j);
        else
            AA(n*j + i,2*n*n+i) = -1 * bucketsup(j);
        end
    end
end


for i =1:n % second constraint in the paper for Beta ij's
    for j=1:3
        AA(n*j + i, 2*n*n+j*n+i) = abs(ym(i));
    end
end



for i =1:n %third constraint for z's
    for j=1:3
        if ym(i) < 0
            AA(4*n + (n*(j-1))+i,2*n*n+i) = -1 * bucketsdown(j);
        else
            AA(4*n + (n*(j-1))+i,2*n*n+i) = bucketsdown(j);
        end
    end
end

for i=1:n %third constraint for Beta ij's
    for j=1:3
        
        AA(4*n + (n*(j-1))+i,2*n*n+j*n+i) = 2/3 - abs(ym(i));
    end
end

for i=1:n
    for j=1:3
        Aeq(i,2*n*n+j*n+i) = 1;
    end
end

for i=1:n
    for j=1:n
        if i~=j
            Aeq(i*n+j,2*(n*(i-1)+j)-1) = 1;
            Aeq(i*n+j,2*(n*(i-1)+j)) = -1;
            Aeq(i*n+j,2*n*n+i) = -1;
            Aeq(i*n+j,2*n*n+j) = 1;
        end
    end
end
Aeq(n*n+n, :) = 0;

for i=1:n %first constraints
    b(i) = -1* M(i) * ym(i);
end

for j=1:3%second constraint
    for i=1:n
        if ym(i) < 0
            b(j*n+i) = M(i) * bucketsup(j);
        else
            b(j*n+i) = -1*M(i) * bucketsup(j);
        end
    end
end

for j=1:3%third constraint
    for i=1:n
        if ym(i)<0
            b(4*n+(j-1)*n+i) = -1*M(i) * bucketsdown(j) + 2/3;
        else
            b(4*n+(j-1)*n+i) = M(i) * bucketsdown(j) + 2/3;
        end
    end
end

for i=1:n %forth constraint, equality constraint
    beq(i)=1;
end

for i=1:n*n %absolute value scores, equality constraint.
    beq(n+i) = 0;
end

lb = zeros(2*n*n+4*n,1);%lower bound for all variable which is 0
ub = ones(2*n*n+4*n,1);%upper bound for all variable which is 1.

[results,fval,EXITFLAG]=linprog(f,AA,b,Aeq,beq,lb,ub);

for i =1:n
    conformity(i) = ym(i)/(results(2*n*n+i) - M(i));
end

