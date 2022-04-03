export type config = {
    gametype: string,
    username: string,
    gametime: number,
    gameincrement: number,
}

export type msgfromSocketHOC = {
    action: string,
    data: any
} | null

export type action = {
    type: string,
    data: any
    message?: any
}