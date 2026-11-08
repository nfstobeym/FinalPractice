class StateTracker {
    constructor() {
        this.steps = 0;
        this.maxSteps = 20;
        this.stack = [];
        this.isFinished = false;
        this.message = "";
    }

    clear() {
        this.steps = 0;
        this.stack = [];
        this.isFinished = false;
        this.message = "";
    }
}

class MatrixEngine {
    constructor() {
        this.grid = [[1, 2, 3], [4, 5, 6], [7, 8, 0]];
        this.emptyPos = { r: 2, c: 2 };
    }

    loadGrid(customGrid) {
        this.grid = customGrid.map(r => [...r]);
        for (let r = 0; r < 3; r++) {
            for (let c = 0; c < 3; c++) {
                if (this.grid[r][c] === 0) {
                    this.emptyPos = { r, c };
                }
            }
        }
    }

    randomize() {
        let flatList = [1, 2, 3, 4, 5, 6, 7, 8, 0];
        let passes = false;
        while (!passes) {
            flatList.sort(() => Math.random() - 0.5);
            let inversions = 0;
            let subset = flatList.filter(n => n !== 0);
            for (let i = 0; i < subset.length; i++) {
                for (let j = i + 1; j < subset.length; j++) {
                    if (subset[i] > subset[j]) inversions++;
                }
            }
            if (inversions % 2 === 0) passes = true;
        }

        let newGrid = [];
        for (let i = 0; i < 3; i++) {
            newGrid.push(flatList.slice(i * 3, i * 3 + 3));
        }
        this.loadGrid(newGrid);
    }

    move(r, c) {
        let validMove = (Math.abs(r - this.emptyPos.r) + Math.abs(c - this.emptyPos.c)) === 1;
        if (!validMove) return false;

        this.grid[this.emptyPos.r][this.emptyPos.c] = this.grid[r][c];
        this.grid[r][c] = 0;
        this.emptyPos = { r, c };
        return true;
    }

    isSorted() {
        let comparison = [1, 2, 3, 4, 5, 6, 7, 8, 0];
        let flatGrid = this.grid.flat();
        return flatGrid.every((val, idx) => val === comparison[idx]);
    }
}

class SliderController {
    constructor() {
        this.engine = new MatrixEngine();
        this.state = new StateTracker();
    }

    limitMaxMoves(num) {
        this.state.maxSteps = num;
    }

    playGame() {
        this.engine.randomize();
        this.state.clear();
    }

    resetGame() {
        this.engine.loadGrid([[1, 2, 3], [4, 5, 6], [7, 8, 0]]);
        this.state.clear();
    }

    clickTile(r, c) {
        if (this.state.isFinished) return;

        let snapshot = this.engine.grid.map(row => [...row]);
        let success = this.engine.move(r, c);

        if (success) {
            this.state.stack.push(snapshot);
            this.state.steps++;
            this.checkConditions();
        }
    }

    undoMove() {
        if (this.state.isFinished || this.state.stack.length === 0) return;
        let lastGrid = this.state.stack.pop();
        this.engine.loadGrid(lastGrid);
        this.state.steps--;
    }

    checkConditions() {
        if (this.engine.isSorted() && this.state.steps < this.state.maxSteps) {
            this.state.message = "Congrats, WINNER!!!";
            this.state.isFinished = true;
        } else if (this.state.steps >= this.state.maxSteps) {
            this.state.message = "You FAILED!";
            this.state.isFinished = true;
        }
    }
}
