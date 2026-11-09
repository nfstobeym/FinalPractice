class SliderMatrixHandler {
    constructor() {
        this.matrix = [[1, 2, 3], [4, 5, 6], [7, 8, 0]];
    }

    locateBlank() {
        for (let r = 0; r < 3; r++) {
            for (let c = 0; c < 3; c++) {
                if (this.matrix[r][c] === 0) return { r, c };
            }
        }
    }

    randomizeBoard() {
        let elements = [1, 2, 3, 4, 5, 6, 7, 8, 0];
        let solved = false;
        while (!solved) {
            for (let i = elements.length - 1; i > 0; i--) {
                let j = Math.floor(Math.random() * (i + 1));
                [elements[i], elements[j]] = [elements[j], elements[i]];
            }
            let inversions = 0;
            let sub = elements.filter(val => val !== 0);
            for (let x = 0; x < sub.length; x++) {
                for (let y = x + 1; y < sub.length; y++) {
                    if (sub[x] > sub[y]) inversions++;
                }
            }
            if (inversions % 2 === 0) solved = true;
        }

        for (let r = 0; r < 3; r++) {
            for (let c = 0; c < 3; c++) {
                this.matrix[r][c] = elements[r * 3 + c];
            }
        }
    }

    attemptSwap(r, c) {
        let blank = this.locateBlank();
        if (Math.abs(r - blank.r) + Math.abs(c - blank.c) === 1) {
            this.matrix[blank.r][blank.c] = this.matrix[r][c];
            this.matrix[r][c] = 0;
            return true;
        }
        return false;
    }

    isSolved() {
        let ref = [[1, 2, 3], [4, 5, 6], [7, 8, 0]];
        return JSON.stringify(this.matrix) === JSON.stringify(ref);
    }
}

class NumberSlider {
    constructor() {
        this.handler = new SliderMatrixHandler();
        this.moves = 0;
        this.maxAllowed = 20;
        this.archive = [];
        this.isFinished = false;
        this.message = "";
    }

    setMaxMoves(max) {
        this.maxAllowed = max;
    }

    playGame() {
        this.handler.randomizeBoard();
        this.moves = 0;
        this.archive = [];
        this.isFinished = false;
        this.message = "";
    }

    resetGame() {
        this.handler.matrix = [[1, 2, 3], [4, 5, 6], [7, 8, 0]];
        this.moves = 0;
        this.archive = [];
        this.isFinished = false;
        this.message = "";
    }

    clickTile(r, c) {
        if (this.isFinished) return;
        let backup = this.handler.matrix.map(row => [...row]);
        if (this.handler.attemptSwap(r, c)) {
            this.archive.push(backup);
            this.moves++;
            this.checkStatus();
        }
    }

    undoMove() {
        if (this.isFinished || this.archive.length === 0) return;
        this.handler.matrix = this.archive.pop();
        this.moves--;
    }

    checkStatus() {
        if (this.handler.isSolved() && this.moves < this.maxAllowed) {
            this.message = "Congrats, WINNER!!!";
            this.isFinished = true;
        } else if (this.moves >= this.maxAllowed) {
            this.message = "You FAILED!";
            this.isFinished = true;
        }
    }
}
