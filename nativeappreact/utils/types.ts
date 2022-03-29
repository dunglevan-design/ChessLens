type config = {
    gametype: string,
    username: string,
    gametime: number,
    gameincrement: number,
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