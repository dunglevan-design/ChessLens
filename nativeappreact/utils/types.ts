type config = {
    isWhite: boolean,
    color: string,
    time: number|string
}

type msgfromSocketHOC = {
    action: string,
    data: any
} | null

type action = {
    type: string,
    data: any
    message?: any
}